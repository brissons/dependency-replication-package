select dependencies.final_data.repo_id, dependencies.final_data.ancestor, datediff("2019-03-01", ghtorrent_restore.projects.created_at) as age
from dependencies.final_data
join ghtorrent_restore.projects
on ghtorrent_restore.projects.id = dependencies.final_data.repo_id
order by dependencies.final_data.repo_name, depth asc