select dependencies.final_data.repo_id, dependencies.final_data.ancestor, coalesce(issue_count, 0) as issue_count
from dependencies.final_data
left join
(
	select dependencies.final_data_issues.repo_id, count(dependencies.final_data_issues.id) as issue_count
	from dependencies.final_data_issues
	join final_data_users
	on dependencies.final_data_issues.reporter_id = final_data_users.user_id
	where dependencies.final_data_issues.repo_id = final_data_users.repo_id
	group by repo_id
) as issues_repo
on dependencies.final_data.repo_id = issues_repo.repo_id
order by dependencies.final_data.repo_name, dependencies.final_data.ancestor asc