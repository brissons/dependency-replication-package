select dependencies.final_data.repo_id, dependencies.final_data.ancestor, coalesce(issue_count, 0) as issue_count
from dependencies.final_data
left join
(
	select family_issues.repo_id, count(family_issues.id) as issue_count
    from 
    (
		select distinctrow issues.*
		from 
        (
			select dependencies.final_data_issues.*, ancestor
            from dependencies.final_data_issues
            join dependencies.final_data_users
            on dependencies.final_data_issues.reporter_id = dependencies.final_data_users.user_id 
        ) as issues
		join dependencies.final_data_users
		on issues.reporter_id = dependencies.final_data_users.user_id
		where issues.ancestor = dependencies.final_data_users.ancestor
		and dependencies.final_data_users.user_id not in
		(
			select user_id
			from dependencies.final_data_users
			where issues.repo_id = repo_id
		)
    ) as family_issues
    group by family_issues.repo_id
) as family_issues
on dependencies.final_data.repo_id = family_issues.repo_id
order by dependencies.final_data.repo_name, dependencies.final_data.depth asc