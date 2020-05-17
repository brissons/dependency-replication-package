select dependencies.final_data.repo_id, dependencies.final_data.ancestor, coalesce(follower_count, 0) as followers_in_project
from dependencies.final_data
left join
(
	select dependencies.final_data_users.repo_id, count(follower_id) as follower_count
	from dependencies.final_data_users
	join ghtorrent_restore.followers
	on dependencies.final_data_users.user_id = ghtorrent_restore.followers.user_id
    join dependencies.final_data_users as follower
    on follower.user_id = ghtorrent_restore.followers.follower_id
    where follower.repo_id = dependencies.final_data_users.repo_id
    group by repo_id
) as followers
on dependencies.final_data.repo_id = followers.repo_id
order by repo_name, depth asc