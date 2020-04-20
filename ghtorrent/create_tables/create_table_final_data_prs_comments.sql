#create table dependencies.final_data_prs_comments as
select ghtorrent_restore.issues.pull_request_id, ghtorrent_restore.issue_comments.* 
from ghtorrent_restore.issue_comments
join ghtorrent_restore.issues
on ghtorrent_restore.issue_comments.issue_id = ghtorrent_restore.issues.id
join dependencies.final_data_prs
on ghtorrent_restore.issues.pull_request_id = dependencies.final_data_prs.id