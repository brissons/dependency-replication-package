create table dependencies.final_data_users_two as
#select users who have committed to projects
select distinctrow dependencies.final_data.repo_id, dependencies.final_data.ancestor, ghtorrent_restore.commits.committer_id as user_id
from ghtorrent_restore.users,
dependencies.final_data
inner join ghtorrent_restore.commits
on ghtorrent_restore.commits.project_id = dependencies.final_data.repo_id
where ghtorrent_restore.users.id = ghtorrent_restore.commits.committer_id
and ghtorrent_restore.users.fake is false
union
#select users who have accepted pull requests
select distinctrow dependencies.final_data.repo_id, dependencies.final_data.ancestor, ghtorrent_restore.pull_request_history.actor_id as user_id
from ghtorrent_restore.users,
dependencies.final_data
inner join ghtorrent_restore.pull_requests
on dependencies.final_data.repo_id = ghtorrent_restore.pull_requests.base_repo_id
inner join ghtorrent_restore.pull_request_history
on ghtorrent_restore.pull_requests.id = ghtorrent_restore.pull_request_history.pull_request_id
where ghtorrent_restore.users.id = ghtorrent_restore.pull_request_history.actor_id
and ghtorrent_restore.pull_request_history.action = 'merged'
and ghtorrent_restore.users.fake is false