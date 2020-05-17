select dependencies.final_data.repo_id, dependencies.final_data.ancestor, coalesce(issue_count, 0) as issue_count
from dependencies.final_data
left join
(	
    select outside_issues.repo_id, count(outside_issues.id) as issue_count
	from 
	(
		select final_data_issues.*
		from final_data_issues
		where final_data_issues.id not in
		(
			select family_issues.id
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
		)
        and final_data_issues.id not in
        (
        	select dependencies.final_data_issues.id
			from dependencies.final_data_issues
			join final_data_users
			on dependencies.final_data_issues.reporter_id = final_data_users.user_id
			where dependencies.final_data_issues.repo_id = final_data_users.repo_id
        )
	) as outside_issues
    group by outside_issues.repo_id
) as outside_issues_count
on dependencies.final_data.repo_id = outside_issues_count.repo_id
order by dependencies.final_data.repo_name, depth asc