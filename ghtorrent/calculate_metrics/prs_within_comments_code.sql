select dependencies.final_data.repo_id, dependencies.final_data.ancestor, coalesce(pr_code_comment_count, 0) as pr_code_comment_count
from dependencies.final_data
left join
(
	select head_repo_id, count(comment_id) as pr_code_comment_count
	from dependencies.final_data_prs_comments_code
    join dependencies.final_data_prs
    on dependencies.final_data_prs_comments_code.pull_request_id = dependencies.final_data_prs.id
	where base_repo_id = head_repo_id
    group by head_repo_id
) as pr_within_code_comments
on dependencies.final_data.repo_id = pr_within_code_comments.head_repo_id
order by repo_name, depth asc