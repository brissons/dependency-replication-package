select dependencies.final_data.repo_id, 
dependencies.final_data.ancestor,
issues_subscribed.issues_subscribed, 
issues_unsubscribed.issues_unsubscribed, 
issues_closed.issues_closed, issues_reopened.issues_reopened,
issues_referenced.issues_referenced,
issues_assigned.issues_assigned,
issues_mentioned.issues_mentioned
from dependencies.final_data
left join
(
	select issue_metric.repo_id, count(issue_metric.id) as issues_subscribed
	from
		(
		select family_issues.repo_id,  dependencies.final_data.ancestor,  family_issues.id, ghtorrent_restore.issue_events.action
		from 
		(
			select distinctrow issues.*
			from 
			(
				select dependencies.final_data_issues.*, ancestor
				from dependencies.final_data_issues
				join dependencies.final_data_users
				on dependencies.final_data_issues.reporter_id = dependencies.final_data_users.user_id 
			) as issues
			join dependencies.final_data_users
			on issues.reporter_id = dependencies.final_data_users.user_id
			where issues.ancestor = dependencies.final_data_users.ancestor
			and dependencies.final_data_users.user_id not in
			(
				select user_id
				from dependencies.final_data_users
				where issues.repo_id = repo_id
			)
		) as family_issues
		join ghtorrent_restore.issue_events
		on family_issues.id = ghtorrent_restore.issue_events.issue_id
		join dependencies.final_data_users 
        on ghtorrent_restore.issue_events.actor_id = dependencies.final_data_users.user_id
        join dependencies.final_data
        on dependencies.final_data.repo_id = family_issues.repo_id
		where action="subscribed"
		and dependencies.final_data_users.repo_id =  family_issues.repo_id
	 )as issue_metric
	group by repo_id
) as issues_subscribed
on dependencies.final_data.repo_id = issues_subscribed.repo_id
left join
(
	select issue_metric.repo_id, count(issue_metric.id) as issues_unsubscribed
	from
		(
		select family_issues.repo_id,  dependencies.final_data.ancestor,  family_issues.id, ghtorrent_restore.issue_events.action
		from 
		(
			select distinctrow issues.*
			from 
			(
				select dependencies.final_data_issues.*, ancestor
				from dependencies.final_data_issues
				join dependencies.final_data_users
				on dependencies.final_data_issues.reporter_id = dependencies.final_data_users.user_id 
			) as issues
			join dependencies.final_data_users
			on issues.reporter_id = dependencies.final_data_users.user_id
			where issues.ancestor = dependencies.final_data_users.ancestor
			and dependencies.final_data_users.user_id not in
			(
				select user_id
				from dependencies.final_data_users
				where issues.repo_id = repo_id
			)
		) as family_issues
		join ghtorrent_restore.issue_events
		on family_issues.id = ghtorrent_restore.issue_events.issue_id
		join dependencies.final_data_users 
        on ghtorrent_restore.issue_events.actor_id = dependencies.final_data_users.user_id
        join dependencies.final_data
        on dependencies.final_data.repo_id = family_issues.repo_id
		where action="unsubscribed"
		and dependencies.final_data_users.repo_id =  family_issues.repo_id
	 )as issue_metric
	group by repo_id
) as issues_unsubscribed
on dependencies.final_data.repo_id = issues_unsubscribed.repo_id
left join
(
	select issues_closed.repo_id, count(issues_closed.id) as issues_closed
	from
	(
		select family_issues.repo_id,  dependencies.final_data.ancestor,  family_issues.id, ghtorrent_restore.issue_events.action
		from 
		(
			select distinctrow issues.*
			from 
			(
				select dependencies.final_data_issues.*, ancestor
				from dependencies.final_data_issues
				join dependencies.final_data_users
				on dependencies.final_data_issues.reporter_id = dependencies.final_data_users.user_id 
			) as issues
			join dependencies.final_data_users
			on issues.reporter_id = dependencies.final_data_users.user_id
			where issues.ancestor = dependencies.final_data_users.ancestor
			and dependencies.final_data_users.user_id not in
			(
				select user_id
				from dependencies.final_data_users
				where issues.repo_id = repo_id
			)
		) as family_issues
		join ghtorrent_restore.issue_events
		on family_issues.id = ghtorrent_restore.issue_events.issue_id
		join dependencies.final_data_users 
        on ghtorrent_restore.issue_events.actor_id = dependencies.final_data_users.user_id
        join dependencies.final_data
        on dependencies.final_data.repo_id = family_issues.repo_id
    where action="closed"
	and dependencies.final_data_users.repo_id =  family_issues.repo_id
	 ) as issues_closed
	group by repo_id
) as issues_closed
on dependencies.final_data.repo_id = issues_closed.repo_id
left join
	(
	select issue_metric.repo_id, count(issue_metric.id) as issues_reopened
	from
		(
		select family_issues.repo_id,  dependencies.final_data.ancestor,  family_issues.id, ghtorrent_restore.issue_events.action
		from 
		(
			select distinctrow issues.*
			from 
			(
				select dependencies.final_data_issues.*, ancestor
				from dependencies.final_data_issues
				join dependencies.final_data_users
				on dependencies.final_data_issues.reporter_id = dependencies.final_data_users.user_id 
			) as issues
			join dependencies.final_data_users
			on issues.reporter_id = dependencies.final_data_users.user_id
			where issues.ancestor = dependencies.final_data_users.ancestor
			and dependencies.final_data_users.user_id not in
			(
				select user_id
				from dependencies.final_data_users
				where issues.repo_id = repo_id
			)
		) as family_issues
		join ghtorrent_restore.issue_events
		on family_issues.id = ghtorrent_restore.issue_events.issue_id
		join dependencies.final_data_users 
        on ghtorrent_restore.issue_events.actor_id = dependencies.final_data_users.user_id
        join dependencies.final_data
                on dependencies.final_data.repo_id = family_issues.repo_id
		where action="reopened"
		and dependencies.final_data_users.repo_id =  family_issues.repo_id
	 )as issue_metric
	group by repo_id
) as issues_reopened
on dependencies.final_data.repo_id = issues_reopened.repo_id
left join
(
	select issue_metric.repo_id, count(issue_metric.id) as issues_referenced
	from
		(
		select family_issues.repo_id,  dependencies.final_data.ancestor,  family_issues.id, ghtorrent_restore.issue_events.action
		from 
		(
			select distinctrow issues.*
			from 
			(
				select dependencies.final_data_issues.*, ancestor
				from dependencies.final_data_issues
				join dependencies.final_data_users
				on dependencies.final_data_issues.reporter_id = dependencies.final_data_users.user_id 
			) as issues
			join dependencies.final_data_users
			on issues.reporter_id = dependencies.final_data_users.user_id
			where issues.ancestor = dependencies.final_data_users.ancestor
			and dependencies.final_data_users.user_id not in
			(
				select user_id
				from dependencies.final_data_users
				where issues.repo_id = repo_id
			)
		) as family_issues
		join ghtorrent_restore.issue_events
		on family_issues.id = ghtorrent_restore.issue_events.issue_id
		join dependencies.final_data_users 
        on ghtorrent_restore.issue_events.actor_id = dependencies.final_data_users.user_id
        join dependencies.final_data
        on dependencies.final_data.repo_id = family_issues.repo_id
		where action="referenced"
		and dependencies.final_data_users.repo_id =  family_issues.repo_id
	 )as issue_metric
	group by repo_id
) as issues_referenced
on dependencies.final_data.repo_id = issues_referenced.repo_id
left join
	(
		select issue_metric.repo_id, count(issue_metric.id) as issues_assigned
		from
		(
		select family_issues.repo_id,  dependencies.final_data.ancestor,  family_issues.id, ghtorrent_restore.issue_events.action
		from 
		(
			select distinctrow issues.*
			from 
			(
				select dependencies.final_data_issues.*, ancestor
				from dependencies.final_data_issues
				join dependencies.final_data_users
				on dependencies.final_data_issues.reporter_id = dependencies.final_data_users.user_id 
			) as issues
			join dependencies.final_data_users
			on issues.reporter_id = dependencies.final_data_users.user_id
			where issues.ancestor = dependencies.final_data_users.ancestor
			and dependencies.final_data_users.user_id not in
			(
				select user_id
				from dependencies.final_data_users
				where issues.repo_id = repo_id
			)
		) as family_issues
		join ghtorrent_restore.issue_events
		on family_issues.id = ghtorrent_restore.issue_events.issue_id
		join dependencies.final_data_users 
        on ghtorrent_restore.issue_events.actor_id = dependencies.final_data_users.user_id
        join dependencies.final_data
        on dependencies.final_data.repo_id = family_issues.repo_id
		where action="assigned"
		and dependencies.final_data_users.repo_id =  family_issues.repo_id
	 )as issue_metric
	group by repo_id
) as issues_assigned
on dependencies.final_data.repo_id = issues_assigned.repo_id
left join
	(
		select issue_metric.repo_id, count(issue_metric.id) as issues_mentioned
		from
		(
		select family_issues.repo_id,  dependencies.final_data.ancestor,  family_issues.id, ghtorrent_restore.issue_events.action
		from 
		(
			select distinctrow issues.*
			from 
			(
				select dependencies.final_data_issues.*, ancestor
				from dependencies.final_data_issues
				join dependencies.final_data_users
				on dependencies.final_data_issues.reporter_id = dependencies.final_data_users.user_id 
			) as issues
			join dependencies.final_data_users
			on issues.reporter_id = dependencies.final_data_users.user_id
			where issues.ancestor = dependencies.final_data_users.ancestor
			and dependencies.final_data_users.user_id not in
			(
				select user_id
				from dependencies.final_data_users
				where issues.repo_id = repo_id
			)
		) as family_issues
		join ghtorrent_restore.issue_events
		on family_issues.id = ghtorrent_restore.issue_events.issue_id
		join dependencies.final_data_users 
        on ghtorrent_restore.issue_events.actor_id = dependencies.final_data_users.user_id
        join dependencies.final_data
        on dependencies.final_data.repo_id = family_issues.repo_id
		where action="mentioned"
		and dependencies.final_data_users.repo_id =  family_issues.repo_id
	 )as issue_metric
	group by repo_id
) as issues_mentioned
on dependencies.final_data.repo_id = issues_mentioned.repo_id
order by dependencies.final_data.repo_name, depth asc