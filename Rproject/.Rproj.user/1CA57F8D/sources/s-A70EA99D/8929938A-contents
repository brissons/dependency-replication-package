##--- Load packages ---##
list.of.packages <-
  c("ggplot2", "lubridate", "dplyr", "corrplot", "tidyverse", "flipTime", "remotes")

# list any missing packages
new.packages <-
  list.of.packages[!(list.of.packages %in% installed.packages()[, "Package"])]



# if packages missing --> install
if (length(new.packages) > 0) {
  install.packages(new.packages, dependencies = TRUE)
}
remotes::install_github("Displayr/flipTime", force = T)
# load all packages
lapply(list.of.packages, require, character.only = TRUE)

##--- Load data ---##

dataset<-read.csv("Data/repos_dep_0630.csv", header = T)[c(1:3419),]
type<-read.csv("Data/repos_type_new.csv", header = T)

##--- Transform date columns ---##
dataset$created_at<-as.character(dataset$created_at)
dataset$created_at<-AsDate(dataset$created_at, us.format = T)
dataset$updated_at<-as.character(dataset$updated_at)
dataset$updated_at<-AsDate(dataset$updated_at, us.format = T)
dataset$last_commit_date<-as.character(dataset$last_commit_date)
dataset$last_commit_date<-AsDate(dataset$last_commit_date, us.format = T)

##--- Transform dependencies column ---##
dataset$dependencies<-as.character(dataset$dependencies)
