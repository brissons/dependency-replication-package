import os
import time

import xlrd
import ast
import xlsxwriter
from colorama import Fore, Back, Style


def get_full_name_from_url(url):
    return url.replace('https://api.github.com/repos/', '')


def has_repo(full_name):
    file_name = full_name
    file_name = file_name.replace('/', '_') + '.xlsx'
    print(os.path.isfile(file_name))
    return os.path.isfile(file_name)


def get_parameters(full_name):
    file_name = full_name
    file_name = file_name.replace('/', '_') + '.xlsx'
    print(os.path.isfile(file_name))
    loc = (file_name)
    wb = xlrd.open_workbook(loc)
    sheet = wb.sheet_by_index(1)
    sheet.cell_value(0, 0)

    # get repo_commit
    repo_commit = []
    for i in range(1, sheet.nrows):
        repo_commit.append([str(sheet.cell_value(i, 0)), str(sheet.cell_value(i, 1)), str(sheet.cell_value(i, 2)),
                            str(sheet.cell_value(i, 3))])

    # get parent_commit
    parent_commit = []
    sheet = wb.sheet_by_index(0)
    for i in range(1, sheet.nrows):
        parent_commit.append([str(sheet.cell_value(i, 0)), str(sheet.cell_value(i, 1)), str(sheet.cell_value(i, 2)),
                              str(sheet.cell_value(i, 3))])

    # get parent_pull_request
    import json
    parent_pull_request = []
    from_repos = []
    sheet = wb.sheet_by_index(2)
    for i in range(1, sheet.nrows):
        from_repos.append(str(sheet.cell_value(i, 8)))
        parent_pull_request.append(ast.literal_eval(sheet.cell_value(i, 3)))

    # get parent_pull_request_author
    parent_pull_request_author = []
    sheet = wb.sheet_by_index(3)
    for i in range(1, sheet.nrows):
        parent_pull_request_author.append(str(sheet.cell_value(i, 0)))

    return repo_commit, parent_commit, parent_pull_request, parent_pull_request_author, from_repos


def analyze2(parent_pull_request, from_repos):
    dict_total = dict()
    dict_counters = dict()
    for i, repo in enumerate(from_repos):
        if repo != "not found repository may be this repo deleted":
            if dict_total.get(repo, None) is None:
                dict_total[repo] = set(parent_pull_request[i])
            else:
                dict_total[repo] = set(parent_pull_request[i] + list(dict_total[repo]))
    from_repos = list(set(from_repos))
    for repo in from_repos:
        if repo != "not found repository may be this repo deleted":
            dict_counters[repo] = len(dict_total[repo])
    return dict_total, dict_counters


def analyze(repo_commit, parent_commit, parent_pull_request, parent_pull_request_author):
    dict_total = {}
    counter = 0
    len_repo_commits = len(repo_commit)
    len_parent_commit = len(parent_commit)
    for i in range(len_repo_commits):

        if (repo_commit[len_repo_commits - 1 - i][0] == parent_commit[len_parent_commit - 1 - j][0]) and (
                repo_commit[len_repo_commits - 1 - i][1] == parent_commit[len_parent_commit - 1 - j][1]) and (
                repo_commit[len_repo_commits - 1 - i][0] in parent_pull_request) and (
                repo_commit[len_repo_commits - 1 - i][0] in parent_pull_request_author):
            print(repo_commit[len_repo_commits - 1 - i])
            counter += 1
            break
        for j in range(len_parent_commit):
            pass

    dict_total['counter'] = str(counter)
    dict_total['percent'] = '{:.2f}%'.format(counter / len_repo_commits * 100)
    dict_total['num_of_commits'] = str(len_repo_commits)
    return dict_total


if __name__ == '__main__':
    try:

        loc = ('data_1.xlsx')
        wb = xlrd.open_workbook(loc)
        sheet = wb.sheet_by_index(0)
        sheet.cell_value(0, 0)
        keys = ['full_name', 'repo_id', 'forked_from', 'parent_id', 'hf_parent_id', 'depth', 'hf_depth',
                'is_hard_fork',
                'url', 'counter', 'percent', 'num_of_unique_commits', 'num_of_commits']
        reposes_inf = []
        for i in range(1, sheet.nrows):
            try:
                repo_dict = {}
                full_name = get_full_name_from_url(str(sheet.cell_value(i, 7)))
                print('checking repo: {}'.format(full_name))
                if has_repo(full_name):
                    print('start reading from file: {}'.format(full_name))
                    repo_id = str(sheet.cell_value(i, 0))
                    forked_from = str(sheet.cell_value(i, 1))
                    parent_id = str(sheet.cell_value(i, 2))
                    hf_parent_id = str(sheet.cell_value(i, 3))
                    depth = str(sheet.cell_value(i, 4))
                    hf_depth = str(sheet.cell_value(i, 5))
                    is_hard_fork = str(sheet.cell_value(i, 6))
                    url = str(sheet.cell_value(i, 7))
                    print(full_name)
                    repo_commit, parent_commit, parent_pull_request, parent_pull_request_author, from_repos = \
                        get_parameters(full_name)
                    pub_info, counters = analyze2(
                        parent_pull_request,
                        from_repos
                    )
                    repo_dict['full_name'] = full_name
                    repo_dict['repo_id'] = repo_id
                    repo_dict['forked_from'] = forked_from
                    repo_dict['parent_id'] = parent_id
                    repo_dict['hf_parent_id'] = hf_parent_id
                    repo_dict['depth'] = depth
                    repo_dict['hf_depth'] = hf_depth
                    repo_dict['is_hard_fork'] = is_hard_fork
                    repo_dict['url'] = url

                    all_repo_commits = set()
                    all_parent_commits = set()
                    for commit in repo_commit:
                        all_repo_commits.add(commit[3])
                    for commit in parent_commit:
                        all_parent_commits.add(commit[3])
                    unique_commits = all_repo_commits.difference(all_parent_commits)

                    repo_dict['counter'] = counters.get(full_name, 0)
                    repo_dict['percent'] = '{:.2f}%'.format(
                        counters.get(full_name, 0) / (counters.get(full_name, 0) + len(unique_commits)) * 100)
                    repo_dict['num_of__unique_commits'] = len(unique_commits)
                    repo_dict['num_of_commits'] = len(repo_commit)
                    reposes_inf.append(repo_dict)
                    with open('get_git_qalat.json', 'a') as f:
                        f.writelines(str(repo_dict) + "\n")
                    print(str(repo_dict) + "\n" + str(i))
                else:
                    print('file: {} does not exists yet'.format(full_name))
            except Exception as e:
                print(Fore.RED + str(e))
                print(Back.BLUE + 'error')
                print(Style.RESET_ALL)
        workbook = xlsxwriter.Workbook('get_git_1.xlsx')
        worksheet_public = workbook.add_worksheet("information")
        row = 0
        col = 0
        for i, j in enumerate(keys):
            worksheet_public.write(row, col + i, j)
        for i in reposes_inf:
            row += 1
            for j, key in enumerate(i):
                worksheet_public.write(row, j, str(i.get(key)))
        workbook.close()
    except Exception as e:
        print(Fore.RED + str(e))
        print(Back.BLUE + 'error')
        print(Style.RESET_ALL)
