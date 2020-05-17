select dependencies.final_data.repo_id, dependencies.final_data.ancestor, coalesce(pr_comment_count, 0) as pr_comment_count
from dependencies.final_data
left join
(
	select pr_family.repo_id, count(comment_id) as pr_comment_count
	from
	(
		select repo_id, id as pr_id
		from
		(
			select id, head_repo_id as repo_id
			from dependencies.final_data_prs
			union
			select id, base_repo_id as repo_id
			from dependencies.final_data_prs
		) as all_prs
		where all_prs.id not in
		(
			select id
			from dependencies.final_data_prs
			where base_repo_id = head_repo_id
		)
	) as pr_family
	join ghtorrent_restore.issues
	on ghtorrent_restore.issues.pull_request_id = pr_family.pr_id
	join dependencies.final_data_prs_comments
	on dependencies.final_data_prs_comments.issue_id = ghtorrent_restore.issues.id
    group by pr_family.repo_id
) as pr_family_comments
on dependencies.final_data.repo_id = pr_family_comments.repo_id
order by repo_name, depth asc