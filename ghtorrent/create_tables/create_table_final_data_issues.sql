create table final_data_issues as
select ghtorrent_restore.issues.*
from ghtorrent_restore.issues
join dependencies.final_data
on dependencies.final_data.repo_id = ghtorrent_restore.issues.repo_id
where pull_request = 0