select dependencies.final_data.repo_id as project_id, dependencies.final_data.ancestor, coalesce(pull_requests_within_project, 0) as pr_within_project_count
from dependencies.final_data
left join
(
	select head_repo_id, count(*) as pull_requests_within_project
	from dependencies.final_data_prs
	where base_repo_id = head_repo_id
    group by head_repo_id
) as pull_requests_within_project
on dependencies.final_data.repo_id = pull_requests_within_project.head_repo_id
order by repo_name, depth asc