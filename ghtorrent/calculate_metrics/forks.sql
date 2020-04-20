select dependencies.final_data.repo_id, dependencies.final_data.ancestor, coalesce(total_fork_count.fork_count, 0) as total_fork_count
from dependencies.final_data
left join
(
    select dependencies.final_data.repo_id, count(*) as fork_count
	from  dependencies.final_data
	join ghtorrent_restore.projects
	on dependencies.final_data.repo_id = ghtorrent_restore.projects.forked_from
    group by dependencies.final_data.repo_id
) as total_fork_count
on total_fork_count.repo_id = dependencies.final_data.repo_id
order by dependencies.final_data.repo_name, depth asc