select dependencies.final_data.repo_id, 
dependencies.final_data.ancestor,
issues_subscribed.issues_subscribed, 
issues_unsubscribed.issues_unsubscribed, 
issues_closed.issues_closed, issues_reopened.issues_reopened,
issues_referenced.issues_referenced,
issues_assigned.issues_assigned,
issues_mentioned.issues_mentioned,
pr_mentioned.pr_mentioned
from dependencies.final_data
left join
(
	select issue_metric.repo_id, count(issue_metric.id) as issues_subscribed
	from
		(
		select  dependencies.final_data_issues.repo_id,  dependencies.final_data.ancestor,  dependencies.final_data_issues.id, ghtorrent_restore.issue_events.action
		from dependencies.final_data_issues
		join ghtorrent_restore.issue_events
		on dependencies.final_data_issues.id = ghtorrent_restore.issue_events.issue_id
		join dependencies.final_data_users 
        on ghtorrent_restore.issue_events.actor_id = dependencies.final_data_users.user_id
        join dependencies.final_data
        on dependencies.final_data.repo_id = dependencies.final_data_issues.repo_id
		where action="subscribed"
		and dependencies.final_data_users.repo_id =  dependencies.final_data_issues.repo_id
	 )as issue_metric
	group by repo_id
) as issues_subscribed
on dependencies.final_data.repo_id = issues_subscribed.repo_id
left join
(
	select issue_metric.repo_id, count(issue_metric.id) as issues_unsubscribed
	from
		(
		select dependencies.final_data_issues.repo_id,  dependencies.final_data.ancestor, dependencies.final_data_issues.id, ghtorrent_restore.issue_events.action
		from dependencies.final_data_issues
		join ghtorrent_restore.issue_events
		on dependencies.final_data_issues.id = ghtorrent_restore.issue_events.issue_id
		join dependencies.final_data_users 
        on ghtorrent_restore.issue_events.actor_id = dependencies.final_data_users.user_id
		join dependencies.final_data
        on dependencies.final_data.repo_id = dependencies.final_data_issues.repo_id
		where action="unsubscribed"
		and dependencies.final_data_users.repo_id = dependencies.final_data_issues.repo_id
	 )as issue_metric
	group by repo_id
) as issues_unsubscribed
on dependencies.final_data.repo_id = issues_unsubscribed.repo_id
left join
(
	select issues_closed.repo_id, count(issues_closed.id) as issues_closed
	from
	(
	select dependencies.final_data_issues.repo_id, dependencies.final_data.ancestor, dependencies.final_data_issues.id, ghtorrent_restore.issue_events.action
	from dependencies.final_data_issues
	join ghtorrent_restore.issue_events
	on dependencies.final_data_issues.id = ghtorrent_restore.issue_events.issue_id
	join dependencies.final_data_users 
    on ghtorrent_restore.issue_events.actor_id = dependencies.final_data_users.user_id
	join dependencies.final_data
    on dependencies.final_data.repo_id = dependencies.final_data_issues.repo_id
    where action="closed"
	and dependencies.final_data_users.repo_id = dependencies.final_data_issues.repo_id
	 ) as issues_closed
	group by repo_id
) as issues_closed
on dependencies.final_data.repo_id = issues_closed.repo_id
left join
	(
	select issue_metric.repo_id, count(issue_metric.id) as issues_reopened
	from
		(
		select dependencies.final_data_issues.repo_id, dependencies.final_data.ancestor, dependencies.final_data_issues.id, ghtorrent_restore.issue_events.action
		from dependencies.final_data_issues
		join ghtorrent_restore.issue_events
		on dependencies.final_data_issues.id = ghtorrent_restore.issue_events.issue_id
		join dependencies.final_data_users 
        on ghtorrent_restore.issue_events.actor_id = dependencies.final_data_users.user_id
        join dependencies.final_data
        on dependencies.final_data.repo_id = dependencies.final_data_issues.repo_id
		where action="reopened"
		and dependencies.final_data_users.repo_id = dependencies.final_data_issues.repo_id
	 )as issue_metric
	group by repo_id
) as issues_reopened
on dependencies.final_data.repo_id = issues_reopened.repo_id
left join
(
	select issue_metric.repo_id, count(issue_metric.id) as issues_referenced
	from
		(
		select dependencies.final_data_issues.repo_id, dependencies.final_data.ancestor, dependencies.final_data_issues.id, ghtorrent_restore.issue_events.action
		from dependencies.final_data_issues
		join ghtorrent_restore.issue_events
		on dependencies.final_data_issues.id = ghtorrent_restore.issue_events.issue_id
		join dependencies.final_data_users 
        on ghtorrent_restore.issue_events.actor_id = dependencies.final_data_users.user_id
        join dependencies.final_data
        on dependencies.final_data.repo_id = dependencies.final_data_issues.repo_id
		where action="referenced"
		and dependencies.final_data_users.repo_id = dependencies.final_data_issues.repo_id
	 )as issue_metric
	group by repo_id
) as issues_referenced
on dependencies.final_data.repo_id = issues_referenced.repo_id
left join
	(
		select issue_metric.repo_id, count(issue_metric.id) as issues_assigned
		from
		(
		select dependencies.final_data_issues.repo_id, dependencies.final_data.ancestor, dependencies.final_data_issues.id, ghtorrent_restore.issue_events.action
		from dependencies.final_data_issues
		join ghtorrent_restore.issue_events
		on dependencies.final_data_issues.id = ghtorrent_restore.issue_events.issue_id
		join dependencies.final_data_users 
        on ghtorrent_restore.issue_events.actor_id = dependencies.final_data_users.user_id
        join dependencies.final_data
        on dependencies.final_data.repo_id = dependencies.final_data_issues.repo_id
		where action="assigned"
		and dependencies.final_data_users.repo_id = dependencies.final_data_issues.repo_id
	 )as issue_metric
	group by repo_id
) as issues_assigned
on dependencies.final_data.repo_id = issues_assigned.repo_id
left join
	(
		select issue_metric.repo_id, count(issue_metric.id) as issues_mentioned
		from
		(
		select dependencies.final_data_issues.repo_id, dependencies.final_data.ancestor, dependencies.final_data_issues.id, ghtorrent_restore.issue_events.action
		from dependencies.final_data_issues
		join ghtorrent_restore.issue_events
		on dependencies.final_data_issues.id = ghtorrent_restore.issue_events.issue_id
		join dependencies.final_data_users 
        on ghtorrent_restore.issue_events.actor_id = dependencies.final_data_users.user_id
        join dependencies.final_data
        on dependencies.final_data.repo_id = dependencies.final_data_issues.repo_id
		where action="mentioned"
		and dependencies.final_data_users.repo_id = dependencies.final_data_issues.repo_id
	 )as issue_metric
	group by repo_id
) as issues_mentioned
on dependencies.final_data.repo_id = issues_mentioned.repo_id
left join
	(
		select issue_metric.repo_id, count(issue_metric.iss_id) as pr_mentioned
		from
        (
        select pr_issues.iss_repo_id as repo_id, dependencies.final_data.ancestor, pr_issues.iss_id, ghtorrent_restore.issue_events.action
        from
		(
			select ghtorrent_restore.issues.id as iss_id, ghtorrent_restore.issues.repo_id as iss_repo_id,  dependencies.final_data_prs.* 
			from dependencies.final_data_prs 
			join ghtorrent_restore.issues
			on ghtorrent_restore.issues.pull_request_id = dependencies.final_data_prs.id
		) as pr_issues
		join ghtorrent_restore.issue_events
		on pr_issues.iss_id = ghtorrent_restore.issue_events.issue_id
        join dependencies.final_data_users
        on ghtorrent_restore.issue_events.actor_id = dependencies.final_data_users.user_id
		join dependencies.final_data
        on dependencies.final_data.repo_id = pr_issues.iss_repo_id
		where action="mentioned"
		and dependencies.final_data_users.repo_id = pr_issues.iss_repo_id
	 )as issue_metric
	group by repo_id
) as pr_mentioned
on dependencies.final_data.repo_id = pr_mentioned.repo_id
order by dependencies.final_data.repo_name, depth asc