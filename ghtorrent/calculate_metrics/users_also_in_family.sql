#checked; no users contribute to other families, so this query is valid
select dependencies.final_data.repo_id, dependencies.final_data.ancestor, coalesce(user_count, 0) as user_count
from dependencies.final_data
left join
(
	select dependencies.final_data_users.repo_id, count(*) as user_count
	from dependencies.final_data_users
	where dependencies.final_data_users.user_id in
	(
		select family_user_id from
		(
			select ancestor, user_id as family_user_id, count(*) as count
			from dependencies.final_data_users
			group by ancestor, user_id
			having count > 1
		) as duplicate_users
	) 
	group by repo_id
)as users_per_repo_also_in_family
on dependencies.final_data.repo_id = users_per_repo_also_in_family.repo_id
order by dependencies.final_data.repo_name, depth asc