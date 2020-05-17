#subtract the total from the followers in the family to get the total amount of followers from outside the family
select dependencies.final_data.repo_id, dependencies.final_data.ancestor, coalesce(follower_count, 0) as follower_count
from dependencies.final_data
left join
(
	select dependencies.final_data_users.repo_id, count(*) as follower_count
	from dependencies.final_data_users
	join ghtorrent_restore.followers
	on dependencies.final_data_users.user_id = ghtorrent_restore.followers.user_id
	group by dependencies.final_data_users.repo_id
) as total_followers
on dependencies.final_data.repo_id = total_followers.repo_id
order by repo_name, depth asc