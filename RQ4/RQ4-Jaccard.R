library(Hmisc)
library(scales)
library(stats)
library(relaimpo)
library(rsq)
library(DescTools)
require(rms)
library(nnet)
library(car)

#RQ4 a) communication vs Jaccard

metrics <- read.csv("C:/Users/mahsa/Desktop/ESEM/ActivityJaccardType.csv", header=TRUE, sep=",", dec=".", fileEncoding="utf-8")

metrics$is_hard_fork  <- as.factor(metrics$is_hard_fork)
metrics$activity  <- as.factor(metrics$activity)
metrics$activity_slope  <- as.factor(metrics$activity_slope)
metrics$activityDensity  <- as.factor(metrics$activityDensity)
metrics$activity_new_ALL  <- as.factor(metrics$activity_new_ALL)

predictor_clustering <- varclus(~ #depth+
                                  
                                  forks +
                                  forks_active +
                                  user_count +
                                  user_family_count +
                                  followers_repo +
                                  followers_in_family +
                                  followers_outside +
                                  pr_within +
                                  pr_family +
                                  pr_within_comments +
                                  pr_family_comments +
                                  pr_within_code_comments +
                                  pr_family_code_comments +
                                  issue_repo	+
                                  issue_family +
                                  issue_outside +
                                  issue_repo_comments +
                                  issue_family_comments +
                                  issue_outside_comments +
                                  issues_subscribed_repo +
                                  #issues_unsubscribed_repo +
                                  issues_closed_repo +
                                  issues_reopened_repo +
                                  issues_referenced_repo +
                                  issues_assigned_repo +
                                  issues_mentioned_repo +
                                  #pr_mentioned_repo +
                                  issues_subscribed_family +
                                  #new_jaccard +
                                  issues_closed_family +
                                  issues_reopened_family +
                                  issues_referenced_family +
                                  issues_assigned_family +
                                  issues_mentioned_family +
                                  issues_subscribed_outside +
                                  #activityDensity +
                                  issues_closed_outside +
                                  issues_reopened_outside +
                                  issues_referenced_outside +
                                  issues_assigned_outside +
                                  issues_mentioned_outside 
                                ,similarity="spearman", data=metrics, trans="abs")

threshold <- 0.7
plot(predictor_clustering)
abline(h = 1 - threshold, col="black", lwd=2, lty=2)
rect.hclust(predictor_clustering$hclust, h=1-threshold, border="red")




#linear regression using raw jaccard numbers
lg = lm(activityDensity ~ 
          depth +
          #age +
          #stars +
          forks +
          forks_active +
          user_count +
          user_family_count +
          followers_repo +
          followers_in_family +
          followers_outside +
          pr_within +
          pr_family +
          pr_within_comments +
          pr_family_comments +
          pr_within_code_comments +
          pr_family_code_comments +
          issue_repo	+
          issue_family +
          issue_outside +
          #issue_repo_comments +
          #issue_family_comments +
          #issue_outside_comments +
          #issues_subscribed_repo +
          #issues_unsubscribed_repo +
          #issues_closed_repo +
          issues_reopened_repo +
          issues_referenced_repo +
          issues_assigned_repo +
          #issues_mentioned_repo +
          #pr_mentioned_repo +
          #issues_subscribed_family +
          #issues_unsubscribed_family +
          #issues_closed_family +
          issues_reopened_family +
          issues_referenced_family +
          issues_assigned_family +
          issues_mentioned_family +
          #issues_subscribed_outside +
          #issues_unsubscribed_outside +
          #issues_closed_outside +
          #issues_reopened_outside +
          #issues_referenced_outside +
          issues_assigned_outside
        , data = metrics)

#results from linear_regression
summary(lg)

#RQ4 a)test using jaccard categories
#this is from the results of the clusting script; 
#found in kmean.csv
lg3 = glm(activityDensity ~ 
            is_hard_fork+
            forks +
            forks_active +
            user_count +
            user_family_count +
            followers_repo +
            followers_in_family +
            followers_outside +
            pr_within +
            pr_family +
            pr_within_comments +
            pr_family_comments +
            pr_within_code_comments +
            pr_family_code_comments +
            issue_repo	+
            issue_family +
            issue_outside +
            #issue_outside_comments +
            #issues_subscribed_repo +
            #issues_unsubscribed_repo +
            #issues_closed_repo +
            issues_reopened_repo +
            issues_referenced_repo +
            issues_assigned_repo +
            #issues_mentioned_repo +
            #pr_mentioned_repo +
            #issues_subscribed_family +
            #issues_unsubscribed_family +
            #issues_closed_family +
            issues_reopened_family +
            issues_referenced_family +
            issues_assigned_family +
            issues_mentioned_family +
            #issues_subscribed_outside +
            #issues_unsubscribed_outside +
            #issues_closed_outside +
            #new_jaccard +
            issues_assigned_outside
          , data = metrics, family="binomial")

#results from linear_regression
summary(lg3)
PseudoR2(lg3, "McFadden")

#RQ4 b) communication vs activity
lg2 = multinom(activity_new_ALL ~ 
                 forks +
                 forks_active +
                 user_count +
                 user_family_count +
                 followers_repo +
                 followers_in_family +
                 followers_outside +
                 pr_within +
                 pr_family +
                 pr_within_comments +
                 pr_family_comments +
                 pr_within_code_comments +
                 pr_family_code_comments +
                 issue_repo	+
                 issue_family +
                 issue_outside +
                 #issue_repo_comments +
                 #issue_family_comments +
                 #issue_outside_comments +
                 #issues_subscribed_repo +
                 #issues_unsubscribed_repo +
                 #issues_closed_repo +
                 issues_reopened_repo +
                 issues_referenced_repo +
                 issues_assigned_repo +
                 #issues_mentioned_repo +
                 #pr_mentioned_repo +
                 #issues_subscribed_family +
                 #issues_unsubscribed_family +
                 #issues_closed_family +
                 issues_reopened_family +
                 issues_referenced_family +
                 issues_assigned_family +
                 issues_mentioned_family +
                 #issues_subscribed_outside +
                 issues_assigned_outside 
               ,data = metrics
               ,model = TRUE)

summary(lg2)
PseudoR2(lg2, "McFadden")
Anova(lg2, type="III")
