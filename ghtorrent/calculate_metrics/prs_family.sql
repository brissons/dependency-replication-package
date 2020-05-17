select dependencies.final_data.repo_id, dependencies.final_data.ancestor, coalesce(pr_family, 0) as pr_family
from dependencies.final_data
left join
(
select repo_id, ancestor, sum(pr_family) as pr_family
from
(
	select head_repo_id as repo_id, ancestor, count(*) as pr_family
	from final_data_prs
	join 
	(
		select distinct id
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
	on final_data_prs.id = pr_family.id
	group by head_repo_id
	union
	select base_repo_id as repo_id, ancestor, count(*) as pr_family
	from final_data_prs
	join 
	(
		select distinct id
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
	on final_data_prs.id = pr_family.id
	group by base_repo_id
) as prs
group by repo_id
) as pr
on dependencies.final_data.repo_id = pr.repo_id