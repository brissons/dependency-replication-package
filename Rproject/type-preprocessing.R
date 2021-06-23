library(dplyr)
library(ggplot2)

results <- read.csv("Data/results.csv", header = T)
type <- {
}
type <- cbind(type, results$repo_id)

type <- cbind(type, results$parent_id)
type <- cbind(type, results$is_hard_fork)
type <- cbind(type, results$ï..full_name)
type <- cbind(type, results$counter)
type <- cbind(type, results$percent_with_unique_commits_sha)
colnames(type) <-
  c("repo_id",
    "parent_id",
    "is_hard_fork",
    "full_name",
    "counter",
    "percentage")
type <- as.data.frame(type)
type$percentage <- as.numeric(type$percentage)
type$repo_id <- as.numeric(type$repo_id)
type$parent_id <- as.numeric(type$parent_id)
type <- full_join(type, repos[, c(1, 3)])


ggplot(type, aes(x = percentage)) + geom_histogram()

type <-
  mutate(
    type,
    ALL = rep("Hard", nrow(type)),
    X0.75 = rep("Hard", nrow(type)),
    X0.5 = rep("Hard", nrow(type)),
    X0.25 = rep("Hard", nrow(type)),
    X1.Commit = rep("Hard", nrow(type))
  )

for (i in 1:nrow(type)) {
  if (type[i, ]$repo_id == type[i, ]$parent_id) {
    type[i, ]$ALL <- "Parent"
    type[i, ]$X0.75 <- "Parent"
    type[i, ]$X0.5 <- "Parent"
    type[i, ]$X0.25 <- "Parent"
    type[i, ]$X1.Commit <- "Parent"
  }
  else{
    if (is.na(type[i, ]$percentage)) {
      type[i, ]$ALL <- "Dead?"
      type[i, ]$X0.75 <- "Dead?"
      type[i, ]$X0.5 <- "Dead?"
      type[i, ]$X0.25 <- "Dead?"
      type[i, ]$X1.Commit <- "Dead?"
    }
    else {
      if (type[i, ]$percentage == 1.0) {
        type[i, ]$ALL <- "Social"
      }
      if (type[i, ]$percentage >= 0.75) {
        type[i, ]$X0.75 <- "Social"
      }
      if (type[i, ]$percentage >= 0.5) {
        type[i, ]$X0.5 <- "Social"
      }
      if (type[i, ]$percentage >= 0.25) {
        type[i, ]$X0.25 <- "Social"
      }
      if (type[i, ]$percentage > 0) {
        type[i, ]$X1.Commit <- "Social"
      }
    }
    
    
  }
}
