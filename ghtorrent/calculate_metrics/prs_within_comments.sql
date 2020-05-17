select dependencies.final_data.repo_id, dependencies.final_data.ancestor, coalesce(comment_count, 0) as comment_count
from dependencies.final_data
left join
(
    select head_repo_id as repo_id, count(dependencies.final_data_prs_comments.comment_id) as comment_count
	from dependencies.final_data_prs
	join ghtorrent_restore.issues
    on ghtorrent_restore.issues.pull_request_id = dependencies.final_data_prs.id
    join dependencies.final_data_prs_comments
    on dependencies.final_data_prs_comments.issue_id = ghtorrent_restore.issues.id
	where base_repo_id = head_repo_id
    group by repo_id
) as pr_comments_within
on dependencies.final_data.repo_id = pr_comments_within.repo_id
order by repo_name, depth asc