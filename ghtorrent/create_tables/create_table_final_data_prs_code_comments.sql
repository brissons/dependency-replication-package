create table final_data_prs_comments_code as
select ghtorrent_restore.pull_request_comments.* 
from ghtorrent_restore.pull_request_comments
join  dependencies.final_data_prs
on ghtorrent_restore.pull_request_comments.pull_request_id = dependencies.final_data_prs.id