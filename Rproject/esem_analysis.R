
#======= Install Dependencies =============
list.of.packages <-
  c("ggplot2", "lubridate", "dplyr", "corrplot", "tidyverse", "flipTime", "remotes", "caret")

# list any missing packages
new.packages <-
  list.of.packages[!(list.of.packages %in% installed.packages()[, "Package"])]
# if packages missing --> install
if (length(new.packages) > 0) {
  install.packages(new.packages, dependencies = TRUE)
}
#remotes::install_github("Displayr/flipTime", force = T)
# load all packages
lapply(list.of.packages, require, character.only = TRUE)

#============= Import and Prepare Datasets =======================

comm<-read.csv("Data/repos_comm.csv", header = T, stringsAsFactors = F)
type<-read.csv("Data/repos_type.csv", header = T, stringsAsFactors = F)

repos<-read.csv("Data/repos.csv", header = T, stringsAsFactors = F)
dep<-read.csv("Data/repos_dep.csv", header = T, stringsAsFactors = F)

to_remove<-repos[which(repos$removed=="TRUE"),]
comm<-comm[-which(comm$repo_id %in% to_remove$repo_id),]
repos<-repos[-which(repos$repo_id %in% to_remove$repo_id),]
dep<-dep[-which(dep$repo_id %in% to_remove$repo_id),]

dataset<-full_join(repos, comm, by=c("repo_id","parent_id","hf_parent_id", "depth", "hf_depth", "is_hard_fork"))
dataset<-mutate(dataset, full_name=character(nrow(dataset)))

for(i in 1:nrow(dataset)) {
  dataset$full_name[i]<-paste(strsplit(dataset$url[i], split="/")[[1]][5], strsplit(dataset$url[i], split="/")[[1]][6], sep="/")
}


type<-type[,-which(colnames(type) %in% c("forked_from", "hf_parent_id", "depth", "hf_depth", "is_hard_fork", "owner_id", "name", "created_at", "updated_at", "build_file"))]

# ================================ Calculate Activity Density and Jaccard Distance for Dependencies ==============================

dataset<-mutate(dataset, activityDensity=dataset$commit_count/dataset$age)
dataset<-mutate(dataset, densitybins=case_when(dataset$activityDensity>=1~1, dataset$activityDensity>=0.14 & dataset$activityDensity<1~2, dataset$activityDensity>=0.03 & dataset$activityDensity<0.14~3, dataset$activityDensity>=0.01 & dataset$activityDensity<0.03~4, dataset$activityDensity>=0.02 & dataset$activityDensity<0.01~5, dataset$activityDensity<0.02~6))
dataset$densitybins<-as.factor(dataset$densitybins)
dataset$depth<-as.factor(dataset$depth)

dataset<-mutate(dataset, new_jaccard=numeric(nrow(dataset)))

for(i in unique(dep$parent_id)) {
  print(i)
  family<-dep[which(dep$parent_id==i),]
  base_deps<-strsplit(family[which(family$depth==0),]$dependencies, ";")[[1]]
  base_deps<-str_trim(base_deps[base_deps!=" "])
  for(k in 1:length(base_deps)) {
    components<-strsplit(base_deps[k], ":")[[1]]
    if(length(components) > 1) {
      base_deps[k]<-components[1:(length(components)-1)]
    }
  }
  
  for(j in 1:nrow(family)) {
    repo_id<-family[j,]$repo_id
    dependencies<-family[j,]$dependencies
    deps<-strsplit(dependencies, ";")[[1]]
    deps<-str_trim(deps[deps!=" "])
    for(k in 1:length(deps)) {
      components<-strsplit(deps[k], ":")[[1]]
      if(length(components) > 1) {
        deps[k]<-components[1:(length(components)-1)]
      }
    }
    jaccard<-1-(length(intersect(base_deps, deps))/length(union(base_deps, deps)))
    dataset[which(dataset$repo_id==repo_id),]$new_jaccard<-jaccard
  }
}

qplot(dataset[which(dataset$new_jaccard>0),]$new_jaccard, xlab = "Jaccard distance", ylab = "Number of Forks", bins=50)

ggplot(data=dataset[which(dataset$new_jaccard>0),], aes(new_jaccard, fill=depth)) + geom_histogram() + theme_bw()

# ================= Test: Jaccard Distance/Acticity Density vs Depth in Family Tree ======================
dataset2<-dataset[-which(dataset$depth==0),]
test2<-aov(new_jaccard~depth, data=dataset2)
summary(test2)
TukeyHSD(test2)
plot(test2, 1)
library(car)
leveneTest(new_jaccard~depth, data=dataset2)
plot(test2, 2)
test2_residuals<-residuals(object = test2)
shapiro.test(x=test2_residuals)
test3<-kruskal.test(new_jaccard~depth, data=dataset2)
test3

dataset2<-dataset[-which(dataset$depth==0),]
test2<-aov(activityDensity~depth, data=dataset2)
summary(test2)
TukeyHSD(test2)
plot(test2, 1)
library(car)
leveneTest(activityDensity~depth, data=dataset2)
plot(test2, 2)
test2_residuals<-residuals(object = test2)
shapiro.test(x=test2_residuals)
test3<-kruskal.test(activityDensity~depth, data=dataset2)
test3


dataset<-mutate(dataset, median_age=numeric(nrow(dataset)))
dataset<-mutate(dataset, median_commits=numeric(nrow(dataset)))
dataset<-mutate(dataset, activity=character(nrow(dataset)))

for(i in unique(dataset$parent_id)) {
  print(i)
  family<-dataset[which(dataset$parent_id==i),]
  
  median_age<-median(family$age)
  median_commits<-median(family$commit_count)
  
  dataset[which(dataset$parent_id==i),]$median_age<-rep(median_age, nrow(family))
  dataset[which(dataset$parent_id==i),]$median_commits<-rep(median_commits, nrow(family))
  
  for(j in 1:nrow(family)) {
    repo_id<-family[j,]$repo_id
    if(dataset[which(dataset$repo_id==repo_id),]$age<=median_age) {
      if(dataset[which(dataset$repo_id==repo_id),]$commit_count<=median_commits) {
        dataset[which(dataset$repo_id==repo_id),]$activity<-"Short-Low"
      }
      else {
        dataset[which(dataset$repo_id==repo_id),]$activity<-"Short-Heavy"
      }
    }
    else {
      if(dataset[which(dataset$repo_id==repo_id),]$commit_count<=median_commits) {
        dataset[which(dataset$repo_id==repo_id),]$activity<-"Long-Low"
      }
      else {
        dataset[which(dataset$repo_id==repo_id),]$activity<-"Long-Heavy"
      }
    }
  }
}
dataset$activity<-as.factor(dataset$activity)

# ===================== Plots about Activity Density and Jaccard Distance =====================

for(i in unique(dataset$parent_id)) {
  print(i)
  family<-dataset[which(dataset$parent_id==i),]
  
  pdf(paste0("Plots/Scatter/",i,".pdf"))
  print(ggplot(data=family, aes(x=age, y=commit_count))+geom_point(aes(colour=depth))+geom_smooth(data=family[-which(family$depth==0),]))
  dev.off()

  pdf(paste0("Plots/Dotplot/", i, ".pdf"))
  print(qplot(densitybins, new_jaccard, data = family, 
        geom = c("dotplot"), stackdir = "center", stackratio=0.2, binaxis = "y",
        color = depth, fill = depth))
  dev.off()
  
  pdf(paste0("Plots/Histo/", i, ".pdf"))
  print(qplot(activity, data = family))
  dev.off()
}

# =================== Evaluate direction of activity (age to #commits) (positive or negative slope) ===========

dataset<-mutate(dataset, activity_direction=character(nrow(dataset)))
dataset<-mutate(dataset, activity_slope=numeric(nrow(dataset)))

normal<-c()
abnormal<-c()
for(i in unique(dataset$parent_id)) {
  print(i)
  family<-dataset[which(dataset$parent_id==i),]
  
  slope<-lm(commit_count~age, data = family)$coefficients[2]
  dataset[which(dataset$parent_id==i),]$activity_slope<-rep(slope, nrow(family))
  
  t<-table(family$activity)
  print(t)
  if(t[2]<t[1] || t[3]<t[4]) {
    normal<-c(normal,i)
    dataset[which(dataset$parent_id==i),]$activity_direction<-rep("Positive", nrow(family))
  }
  else {
    abnormal<-c(abnormal, i)
    dataset[which(dataset$parent_id==i),]$activity_direction<-rep("Negative", nrow(family))
  }
}

dataset$activity_direction<-as.factor(dataset$activity_direction)

families<-unique(dataset[,c("parent_id","activity_direction","activity_slope")])
families<-arrange(families, activity_slope)
families$parent_id<-as.factor(families$parent_id)

ggplot(data=families, aes(x=seq_along(parent_id), y=activity_slope, fill=activity_direction))+geom_bar(stat="identity")

# =================== Type Analysis =======================

types_summarized<-{}

type$parent_id<-as.factor(type$parent_id)
type_df<-type %>% filter(X1.Commit %in% c("Social", "Hard")) %>% group_by(parent_id, X1.Commit) %>% summarise(counts=n()) %>% arrange(counts)
type_df_wide<-spread(type_df, X1.Commit, counts, fill=0)
types_summarized <- rbind(types_summarized, c(class="X1.Commit", colSums(type_df_wide[,-1])[1], colSums(type_df_wide[,-1])[2]))
type_df[which(type_df$X1.Commit=="Social"),]$counts<-type_df[which(type_df$X1.Commit=="Social"),]$counts*(-1)
ggplot(type_df, aes(x=reorder(parent_id,counts), y=counts)) + geom_bar(aes(color=X1.Commit, fill=X1.Commit), stat="identity", position = position_stack())+theme(axis.text.x = element_text(angle=90))

type$parent_id<-as.factor(type$parent_id)
type_df<-type %>% filter(X0.25 %in% c("Social", "Hard")) %>% group_by(parent_id, X0.25) %>% summarise(counts=n())
type_df_wide<-spread(type_df, X0.25, counts, fill=0)
types_summarized <- rbind(types_summarized, c(class="X0.25", colSums(type_df_wide[,-1])[1], colSums(type_df_wide[,-1])[2]))
type_df[which(type_df$X0.25=="Social"),]$counts<-type_df[which(type_df$X0.25=="Social"),]$counts*(-1)
ggplot(type_df, aes(x=reorder(parent_id,counts), y=counts)) + geom_bar(aes(color=X0.25, fill=X0.25), stat="identity", position = position_stack())+theme(axis.text.x = element_text(angle=90))

type$parent_id<-as.factor(type$parent_id)
type_df<-type %>% filter(X0.75 %in% c("Social", "Hard")) %>% group_by(parent_id, X0.75) %>% summarise(counts=n())
type_df_wide<-spread(type_df, X0.75, counts, fill=0)
types_summarized <- rbind(types_summarized, c(class="X0.75", colSums(type_df_wide[,-1])[1], colSums(type_df_wide[,-1])[2]))
type_df[which(type_df$X0.75=="Social"),]$counts<-type_df[which(type_df$X0.75=="Social"),]$counts*(-1)
ggplot(type_df, aes(x=reorder(parent_id,counts), y=counts)) + geom_bar(aes(color=X0.75, fill=X0.75), stat="identity", position = position_stack())+theme(axis.text.x = element_text(angle=90))

type$parent_id<-as.factor(type$parent_id)
type_df<-type %>% filter(X0.5 %in% c("Social", "Hard")) %>% group_by(parent_id, X0.5) %>% summarise(counts=n())
type_df_wide<-spread(type_df, X0.5, counts, fill=0)
types_summarized <- rbind(types_summarized, c(class="X0.5", colSums(type_df_wide[,-1])[1], colSums(type_df_wide[,-1])[2]))
type_df[which(type_df$X0.5=="Social"),]$counts<-type_df[which(type_df$X0.5=="Social"),]$counts*(-1)
ggplot(type_df, aes(x=reorder(parent_id,counts), y=counts)) + geom_bar(aes(color=X0.5, fill=X0.5), stat="identity", position = position_stack())+theme(axis.text.x = element_text(angle=90))

type$parent_id<-as.factor(type$parent_id)
type_df<-type %>% filter(ALL %in% c("Social", "Hard")) %>% group_by(parent_id, ALL) %>% summarise(counts=n())
type_df_wide<-spread(type_df, ALL, counts, fill=0)
types_summarized <- rbind(types_summarized, c(class="ALL", colSums(type_df_wide[,-1])[1], colSums(type_df_wide[,-1])[2]))
type_df[which(type_df$ALL=="Social"),]$counts<-type_df[which(type_df$ALL=="Social"),]$counts*(-1)
ggplot(type_df, aes(x=reorder(parent_id,counts), y=counts)) + geom_bar(aes(color=ALL, fill=ALL), stat="identity", position = position_stack())+theme(axis.text.x = element_text(angle=90))

types_summarized_long<-gather(as.data.frame(types_summarized), Type, count, Hard:Social)
types_summarized_long$count<-as.numeric(types_summarized_long$count)
types_summarized_long$class<-factor(types_summarized_long$class, levels=c("X1.Commit", "X0.25", "X0.5", "X0.75", "ALL"))
types_summarized_long<-types_summarized_long %>% arrange(class)
ggplot(types_summarized_long, aes(x=class, y=count)) + geom_bar(aes(color=Type, fill=Type), stat="identity", position = position_stack())#+theme(axis.text.x = element_text(angle=90))


dataset$parent_id<-as.factor(dataset$parent_id)
dataset2<-left_join(dataset, type, by=c("repo_id", "parent_id"))

# ================= Type comparison with ICSE2020 =========================

types_comb<-dataset2[,c(1,7,73:77)]
types_comb<-types_comb %>% filter(X1.Commit %in% c("Social", "Hard")) %>% filter(X0.25 %in% c("Social", "Hard")) %>% filter(X0.75 %in% c("Social", "Hard")) %>% filter(X0.5 %in% c("Social", "Hard")) %>% filter(ALL %in% c("Social", "Hard"))
types_comb[which(types_comb$is_hard_fork==0),]$is_hard_fork<-"Social"
types_comb[which(types_comb$is_hard_fork==1),]$is_hard_fork<-"Hard"

cm_tab<-{}

truth<-as.factor(types_comb$is_hard_fork)
pred<-as.factor(types_comb$X1.Commit)
cm<-confusionMatrix(truth, pred)
cm_tab<-rbind(cm_tab, c("X1.Commit", cm$byClass[5], cm$byClass[6], cm$byClass[4]))

truth<-as.factor(types_comb$is_hard_fork)
pred<-as.factor(types_comb$X0.25)
cm<-confusionMatrix(truth, pred)
cm_tab<-rbind(cm_tab, c("X0.25", cm$byClass[5], cm$byClass[6], cm$byClass[4]))

truth<-as.factor(types_comb$is_hard_fork)
pred<-as.factor(types_comb$X0.75)
cm<-confusionMatrix(truth, pred)
cm_tab<-rbind(cm_tab, c("X0.75", cm$byClass[5], cm$byClass[6], cm$byClass[4]))

truth<-as.factor(types_comb$is_hard_fork)
pred<-as.factor(types_comb$X0.5)
cm<-confusionMatrix(truth, pred)
cm_tab<-rbind(cm_tab, c("X0.5", cm$byClass[5], cm$byClass[6], cm$byClass[4]))

truth<-as.factor(types_comb$is_hard_fork)
pred<-as.factor(types_comb$ALL)
cm<-confusionMatrix(truth, pred)
cm_tab<-rbind(cm_tab, c("ALL", cm$byClass[5], cm$byClass[6], cm$byClass[4]))

# =============== Significant difference per family in Activity Density and Jaccard Distance according to fork type ===============

significant_jaccard_familiesALL<-c()
insignificant_jaccard_familiesALL<-c()
significant_activity_familiesALL<-c()
insignificant_activity_familiesALL<-c()
onedimension_familiesALL<-c()
for(i in unique(dataset2$parent_id)) {
  print(i)
  family<-dataset2[which(dataset2$parent_id==i),]
  
  family<-family[-which(family$ALL=="Parent" | is.na(family$ALL)),]
  
  family$ALL<-factor(family$ALL)
  
  if(length(levels(family$ALL))==2) {
    test<-wilcox.test(new_jaccard~ALL, data=family)
    if(!is.na(test$p.value) && test$p.value<0.025) {
      significant_jaccard_familiesALL<-c(significant_jaccard_familiesALL, i)
    }
    else {
      insignificant_jaccard_familiesALL<-c(insignificant_jaccard_familiesALL, i)
    }
    
    test<-wilcox.test(activityDensity~ALL, data=family)
    if(!is.na(test$p.value) && test$p.value<0.025) {
      significant_activity_familiesALL<-c(significant_activity_familiesALL, i)
    }
    else {
      insignificant_activity_familiesALL<-c(insignificant_activity_familiesALL, i)
    }
  }
  else {
    onedimension_familiesALL<-c(onedimension_familiesALL, i)
  }
}

length(significant_jaccard_familiesALL)
length(insignificant_jaccard_familiesALL)
length(significant_activity_familiesALL)
length(insignificant_activity_familiesALL)
length((onedimension_familiesALL))

significant_jaccard_families75<-c()
insignificant_jaccard_families75<-c()
significant_activity_families75<-c()
insignificant_activity_families75<-c()
onedimension_families75<-c()
for(i in unique(dataset2$parent_id)) {
  print(i)
  family<-dataset2[which(dataset2$parent_id==i),]
  
  family<-family[-which(family$X0.75=="Parent" | is.na(family$X0.75)),]
  
  family$X0.7<-factor(family$X0.75)
  
  if(length(levels(family$X0.75))==2) {
    test<-wilcox.test(new_jaccard~X0.75, data=family)
    if(!is.na(test$p.value) && test$p.value<0.025) {
      significant_jaccard_families75<-c(significant_jaccard_families75, i)
    }
    else {
      insignificant_jaccard_families75<-c(insignificant_jaccard_families75, i)
    }
    
    test<-wilcox.test(activityDensity~X0.75, data=family)
    if(!is.na(test$p.value) && test$p.value<0.025) {
      significant_activity_families75<-c(significant_activity_families75, i)
    }
    else {
      insignificant_activity_families75<-c(insignificant_activity_families75, i)
    }
  }
  else {
    onedimension_families75<-c(onedimension_families75, i)
  }
}

length(significant_jaccard_families75)
length(insignificant_jaccard_families75)
length(significant_activity_families75)
length(insignificant_activity_families75)
length((onedimension_families75))

significant_jaccard_families50<-c()
insignificant_jaccard_families50<-c()
significant_activity_families50<-c()
insignificant_activity_families50<-c()
onedimension_families50<-c()
for(i in unique(dataset2$parent_id)) {
  print(i)
  family<-dataset2[which(dataset2$parent_id==i),]
  
  family<-family[-which(family$X0.5=="Parent" | is.na(family$X0.5)),]
  
  family$X0.5<-factor(family$X0.5)
  
  if(length(levels(family$X0.5))==2) {
    test<-wilcox.test(new_jaccard~X0.5, data=family)
    if(!is.na(test$p.value) && test$p.value<0.025) {
      significant_jaccard_families50<-c(significant_jaccard_families50, i)
    }
    else {
      insignificant_jaccard_families50<-c(insignificant_jaccard_families50, i)
    }
    
    test<-wilcox.test(activityDensity~X0.5, data=family)
    if(!is.na(test$p.value) && test$p.value<0.025) {
      significant_activity_families50<-c(significant_activity_families50, i)
    }
    else {
      insignificant_activity_families50<-c(insignificant_activity_families50, i)
    }
  }
  else {
    onedimension_families50<-c(onedimension_families50, i)
  }
}

length(significant_jaccard_families50)
length(insignificant_jaccard_families50)
length(significant_activity_families50)
length(insignificant_activity_families50)
length((onedimension_families50))

significant_jaccard_families25<-c()
insignificant_jaccard_families25<-c()
significant_activity_families25<-c()
insignificant_activity_families25<-c()
onedimension_families25<-c()
for(i in unique(dataset2$parent_id)) {
  print(i)
  family<-dataset2[which(dataset2$parent_id==i),]
  
  family<-family[-which(family$X0.25=="Parent" | is.na(family$X0.25)),]
  
  family$X0.25<-factor(family$X0.25)
  
  if(length(levels(family$X0.25))==2) {
    test<-wilcox.test(new_jaccard~X0.25, data=family)
    if(!is.na(test$p.value) && test$p.value<0.025) {
      significant_jaccard_families25<-c(significant_jaccard_families25, i)
    }
    else {
      insignificant_jaccard_families25<-c(insignificant_jaccard_families25, i)
    }
    
    test<-wilcox.test(activityDensity~X0.25, data=family)
    if(!is.na(test$p.value) && test$p.value<0.025) {
      significant_activity_families25<-c(significant_activity_families25, i)
    }
    else {
      insignificant_activity_families25<-c(insignificant_activity_families25, i)
    }
  }
  else {
    onedimension_families25<-c(onedimension_families25, i)
  }
}

length(significant_jaccard_families25)
length(insignificant_jaccard_families25)
length(significant_activity_families25)
length(insignificant_activity_families25)
length((onedimension_families25))

significant_jaccard_families1<-c()
insignificant_jaccard_families1<-c()
significant_activity_families1<-c()
insignificant_activity_families1<-c()
onedimension_families1<-c()
for(i in unique(dataset2$parent_id)) {
  print(i)
  family<-dataset2[which(dataset2$parent_id==i),]
  
  family<-family[-which(family$X1.Commit=="Parent" | is.na(family$X1.Commit)),]
  
  family$X1.Commit<-factor(family$X1.Commit)
  
  if(length(levels(family$X1.Commit))==2) {
    test<-wilcox.test(new_jaccard~X1.Commit, data=family)
    if(!is.na(test$p.value) && test$p.value<0.05) {
      significant_jaccard_families1<-c(significant_jaccard_families1, i)
    }
    else {
      insignificant_jaccard_families1<-c(insignificant_jaccard_families1, i)
    }
    
    test<-wilcox.test(activityDensity~X1.Commit, data=family)
    if(!is.na(test$p.value) && test$p.value<0.05) {
      significant_activity_families1<-c(significant_activity_families1, i)
    }
    else {
      insignificant_activity_families1<-c(insignificant_activity_families1, i)
    }
  }
  else {
    onedimension_families1<-c(onedimension_families1, i)
  }
}

length(significant_jaccard_families1)
length(insignificant_jaccard_families1)
length(significant_activity_families1)
length(insignificant_activity_families1)
length((onedimension_families1))

common_families_jaccard<-union(significant_jaccard_familiesALL, significant_jaccard_families50)
common_families_jaccard<-union(common_families_jaccard, significant_activity_families75)
common_families_jaccard<-union(common_families_jaccard, significant_activity_families25)
common_families_jaccard<-union(common_families_jaccard, significant_activity_families1)

# ============ Test: Activity Density and Jaccard Distance vs Type =======================

dataset3<-dataset2[-which(dataset2$ALL=="Parent" | dataset2$ALL=="Dead?"),]
dataset3$ALL<-factor(dataset3$ALL)
test<-wilcox.test(activityDensity~ALL, data=dataset3, conf.int=T)
test2<-wilcox.test(new_jaccard~ALL, data=dataset3, conf.int=T)
print(test)
print(test2)

dataset3<-dataset2[-which(dataset2$X0.75=="Parent" | dataset2$X0.75=="Dead?"),]
dataset3$X0.75<-factor(dataset3$X0.75)
test<-wilcox.test(activityDensity~X0.75, data=dataset3, conf.int=T)
test2<-wilcox.test(new_jaccard~X0.75, data=dataset3, conf.int=T)
print(test)
print(test2)

dataset3<-dataset2[-which(dataset2$X0.25=="Parent" | dataset2$X0.25=="Dead?"),]
dataset3$X0.25<-factor(dataset3$X0.25)
test<-wilcox.test(activityDensity~X0.25, data=dataset3, conf.int=T)
test2<-wilcox.test(new_jaccard~X0.25, data=dataset3, conf.int=T)
print(test)
print(test2)

dataset3<-dataset2[-which(dataset2$X0.5=="Parent" | dataset2$X0.5=="Dead?"),]
dataset3$X0.5<-factor(dataset3$X0.5)
test<-wilcox.test(activityDensity~X0.5, data=dataset3, conf.int=T)
test2<-wilcox.test(new_jaccard~X0.5, data=dataset3, conf.int=T)
print(test)
print(test2)

dataset3<-dataset2[-which(dataset2$X1.Commit=="Parent" | dataset2$X1.Commit=="Dead?"),]
dataset3$X1.Commit<-factor(dataset3$X1.Commit)
test<-wilcox.test(activityDensity~X1.Commit, data=dataset3, conf.int=T)
test2<-wilcox.test(new_jaccard~X1.Commit, data=dataset3, conf.int=T)
print(test)
print(test2)

cols<-c("repo_id", "activityDensity", "densitybins", "new_jaccard", "median_age", "median_commits", "activity", "activity_direction", "activity_slope")
dataset4<-dataset2[cols]
write.csv(dataset4, "Results/repos_analysis.csv")

# ============== Additional analyses (ignore for now) ======================

dataset3<-dataset2[-which(dataset2$ALL=="Parent" | dataset2$ALL=="Dead?"),]
dataset3$ALL<-factor(dataset3$ALL)
test<-kruskal.test(cbind(activityDensity, new_jaccard)~ALL, data=dataset3)
summary(test)
summary.aov(test)

tryCatch({
  dataset3<-dataset2[,c("activityDensity", "new_jaccard")]
  #colnames(fam)<-c("comm", "dep", "stars", "forks", "users", "foll", "issues", "age")
  #pdf(paste0("Plots/Corr/",i,".pdf"))
  correlations<-cor(dataset3[which(dataset3$activityDensity>1),], method="kendall")
  ptest<-cor.mtest(dataset3, conf.level=.95)
  corrplot.mixed(correlations, p.mat=ptest$p, sig.level=.05)
  #dev.off()
}, error=function(e){cat("ERROR :",conditionMessage(e), "\n")})

qplot(data=dataset2[which(dataset2$activityDensity<1 & dataset2$depth != 0 & dataset2$depth != 1 & dataset2$depth != 2),], x=activityDensity, y=new_jaccard, colour=depth)


