create table final_data_prs as
select ghtorrent_restore.pull_requests.*, head.ancestor
from ghtorrent_restore.pull_requests
right join dependencies.final_data as head
on head.repo_id = ghtorrent_restore.pull_requests.head_repo_id
right join dependencies.final_data as base
on base.repo_id = ghtorrent_restore.pull_requests.base_repo_id
where ghtorrent_restore.pull_requests.head_repo_id is not null
and ghtorrent_restore.pull_requests.base_repo_id is not null