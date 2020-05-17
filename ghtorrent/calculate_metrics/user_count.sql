select dependencies.final_data.repo_id, dependencies.final_data.ancestor, coalesce(user_count, 0) as user_count
from dependencies.final_data
left join
(
	select repo_id, count(*) as user_count
	from dependencies.final_data_users
	group by repo_id
) as users_per_repo
on dependencies.final_data.repo_id = users_per_repo.repo_id
order by dependencies.final_data.repo_name, depth asc