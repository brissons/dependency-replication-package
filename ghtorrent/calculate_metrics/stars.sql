select dependencies.final_data.repo_id, dependencies.final_data.ancestor, count(ghtorrent_restore.watchers.repo_id) as stars 
from dependencies.final_data
left join ghtorrent_restore.watchers
on ghtorrent_restore.watchers.repo_id = dependencies.final_data.repo_id
group by dependencies.final_data.repo_id
order by repo_name, depth asc