select dependencies.final_data.repo_id, coalesce(comment_count, 0) as comment_count
from dependencies.final_data
left join
(
	select family_comments.repo_id, count(comment_id) as comment_count
    from 
    (
		select distinctrow dependencies.final_data_issues.*, comment_id
		from dependencies.final_data_issues
		join dependencies.final_data_users
		on dependencies.final_data_issues.reporter_id = dependencies.final_data_users.user_id
        join dependencies.final_data_issues_comments
        on dependencies.final_data_issues_comments.issue_id = dependencies.final_data_issues.id
        join dependencies.final_data
        on dependencies.final_data_issues.repo_id = dependencies.final_data.repo_id
		where dependencies.final_data.ancestor = dependencies.final_data_users.ancestor
		and dependencies.final_data_users.user_id not in
		(
			select user_id
			from dependencies.final_data_users
			where dependencies.final_data_issues.repo_id = repo_id
		)
    ) as family_comments
    group by family_comments.repo_id
) as family_comments
on dependencies.final_data.repo_id = family_comments.repo_id
order by dependencies.final_data.repo_name, dependencies.final_data.depth asc