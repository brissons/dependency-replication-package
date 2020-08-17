library(Hmisc)
library(scales)
library(stats)
library(relaimpo)
library(rsq)
library(DescTools)
require(rms)

#RQ3 a) communication vs hard forks
metrics <- read.csv("RQ3-RQ4.csv", header=TRUE, sep=",", dec=".", fileEncoding="utf-8")
metrics$is_hard_fork  <- as.factor(metrics$is_hard_fork)
metrics$is_hf_parent  <- as.factor(metrics$is_hf_parent)
metrics$activity  <- as.factor(metrics$activity)
metrics$all  <- as.factor(metrics$all)
metrics$ninety <- as.factor(metrics$ninety)
metrics$seventy <- as.factor(metrics$seventy)
metrics$thirty  <- as.factor(metrics$thirty)
metrics$one_commit  <- as.factor(metrics$one_commit)
metrics$jaccard_median  <- as.factor(metrics$jaccard_median)
metrics$jaccard_k2  <- as.factor(metrics$jaccard_k2)
metrics$jaccard_k3  <- as.factor(metrics$jaccard_k3)
metrics$jaccard_k4  <- as.factor(metrics$jaccard_k4)
metrics$j_c_2  <- as.factor(metrics$j_c_2)

options(scipen = 999)

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
                                  issues_mentioned_outside
                                ,similarity="spearman", data=metrics, trans="abs")

threshold <- 0.7
plot(predictor_clustering)
abline(h = 1 - threshold, col="black", lwd=2, lty=2)
rect.hclust(predictor_clustering$hclust, h=1-threshold, border="red")

#RQ3 a) communication vs hf
lg = glm(is_hf_parent ~ 
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
           issues_mentioned_repo +
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
           #issues_mentioned_outside
          ,data = metrics, family="binomial")

#results from linear_regression 
summary(lg)
PseudoR2(lg, "McFadden")

#RQ3 b) communication vs all commits
lg1 = glm(all ~ 
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
            issues_mentioned_repo +
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
            #issues_mentioned_outside
          ,data = metrics, family="binomial")

#results from linear_regression 
summary(lg1)
PseudoR2(lg1, "McFadden")

#RQ3 c) communication vs nine commits
lg2 = glm(ninety ~ 
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
            issues_mentioned_repo +
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
            #issues_mentioned_outside
          ,data = metrics, family="binomial")

#results from linear_regression 
summary(lg2)
PseudoR2(lg2, "McFadden")

#RQ3 d) communication vs seven commits
lg3 = glm(seventy ~ 
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
            issues_mentioned_repo +
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
            #issues_mentioned_outside
          ,data = metrics, family="binomial")

#results from linear_regression 
summary(lg3)
PseudoR2(lg3, "McFadden")

#RQ3 e) communication vs three commits
lg4 = glm(thirty ~ 
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
            issues_mentioned_repo +
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
            #issues_mentioned_outside
          ,data = metrics, family="binomial")

#results from linear_regression 
summary(lg4)
PseudoR2(lg4, "McFadden")

#RQ3 f) communication vs 1 commits
lg5 = glm(one_commit ~ 
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
            issues_mentioned_repo +
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
          #issues_mentioned_outside
          ,data = metrics, family="binomial", maxit=50)

#results from linear_regression 
summary(lg5)
PseudoR2(lg5, "McFadden")