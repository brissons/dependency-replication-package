select dependencies.final_data.repo_id, dependencies.final_data.ancestor, coalesce(comment_count, 0) as comment_count
from dependencies.final_data
left join
(
    select dependencies.final_data_issues.repo_id, count(*) as comment_count
	from dependencies.final_data_issues
	join dependencies.final_data_users
	on dependencies.final_data_issues.reporter_id = dependencies.final_data_users.user_id
    join dependencies.final_data_issues_comments
    on dependencies.final_data_issues_comments.issue_id = dependencies.final_data_issues.id
	where dependencies.final_data_issues.repo_id = dependencies.final_data_users.repo_id
    group by dependencies.final_data_issues.repo_id
) as issue_comments
on dependencies.final_data.repo_id = issue_comments.repo_id
order by dependencies.final_data.repo_name, dependencies.final_data.depth asc
