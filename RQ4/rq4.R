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
metrics <- read.csv("RQ3-RQ4.csv", header=TRUE, sep=",", dec=".", fileEncoding="utf-8")
metrics$is_hard_fork  <- as.factor(metrics$is_hard_fork)
metrics$is_hf_parent  <- as.factor(metrics$is_hf_parent)
metrics$activity  <- as.factor(metrics$activity)
metrics$all  <- as.factor(metrics$all)
metrics$nine <- as.factor(metrics$nine)
metrics$seven <- as.factor(metrics$seven)
metrics$thirty  <- as.factor(metrics$thirty)
metrics$one_commit  <- as.factor(metrics$one_commit)
metrics$jaccard_median  <- as.factor(metrics$jaccard_median)
metrics$jaccard_k2  <- as.factor(metrics$jaccard_k2)

predictor_clustering <- varclus(~ depth +
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
                                  #issues_unsubscribed_family +
                                  issues_closed_family +
                                  issues_reopened_family +
                                  issues_referenced_family +
                                  issues_assigned_family +
                                  issues_mentioned_family +
                                  issues_subscribed_outside +
                                  #issues_unsubscribed_outside +
                                  issues_closed_outside +
                                  issues_reopened_outside +
                                  issues_referenced_outside +
                                  issues_assigned_outside +
                                  issues_mentioned_outside +
                                  #is_hf_parent +
                                  all +
                                  thirty + 
                                  seven + 
                                  nine +
                                  one_commit
                                ,similarity="spearman", data=metrics, trans="abs")

threshold <- 0.7
plot(predictor_clustering)
abline(h = 1 - threshold, col="black", lwd=2, lty=2)
rect.hclust(predictor_clustering$hclust, h=1-threshold, border="red")

#linear regression using raw jaccard numbers
lg = lm(new_jaccard ~ 
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
          issues_assigned_outside +
          #issues_mentioned_outside +
          #is_hf_parent +
          all +
          thirty + 
          #seven + 
          #nine +
          one_commit
         , data = metrics)

#results from linear_regression
summary(lg)

#RQ4 a)test using jaccard categories
#this is from the results of the clusting script; 
#found in kmean.csv
lg3 = glm(jaccard_k2 ~ 
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
          issues_assigned_outside +
          #issues_mentioned_outside +
          #is_hf_parent +
          all +
          thirty + 
          #seven + 
          #nine +
          one_commit
        , data = metrics, family="binomial")

#results from linear_regression
summary(lg3)
PseudoR2(lg3, "McFadden")

#RQ4 b) communication vs activity
lg2 = multinom(activity ~ 
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
                 issues_assigned_outside +
                 #issues_mentioned_outside +
                 #is_hf_parent +
                 all +
                 thirty + 
                 #seven + 
                 #nine +
                 one_commit
                 ,data = metrics
                 ,model = TRUE)

summary(lg2)
PseudoR2(lg2, "McFadden")
Anova(lg2, type="III")
