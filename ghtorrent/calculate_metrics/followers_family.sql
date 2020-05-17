#subtract the total from the followers in the repo to get the total amount of followers from inside the family
select dependencies.final_data.repo_id, dependencies.final_data.ancestor, coalesce(follower_count, 0) as followers_in_family
from dependencies.final_data
left join
(
	select dependencies.final_data_users.repo_id, count(follower_id) as follower_count
	from dependencies.final_data_users
	join ghtorrent_restore.followers
	on dependencies.final_data_users.user_id = ghtorrent_restore.followers.user_id
    join 
	(
		select distinctrow user_id, ancestor
		from dependencies.final_data_users
	) as unique_ancestors
    on unique_ancestors.user_id = ghtorrent_restore.followers.follower_id
    where unique_ancestors.ancestor = dependencies.final_data_users.ancestor
    group by repo_id
) as followers
on dependencies.final_data.repo_id = followers.repo_id
order by repo_name, depth asc