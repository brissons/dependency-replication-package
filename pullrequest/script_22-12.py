import os
import time
from datetime import datetime

import requests
import xlrd
import xlsxwriter
from requests.auth import HTTPBasicAuth
from github import Github
from colorama import Fore, Back, Style
import zlib

g_counter = 0
username = [ 'soghac']
password = [ '8cfc66cbda6c52ca0f7c4866e0b909fa18d31cdb']
g = Github(username[g_counter % 3], password[g_counter % 3])


class Repository:
    parent_commits_dict = dict()
    parent_pulls_requests = dict()
    parent_pulls_requests_authors = dict()
    list_authors = set()
    june_2019 = datetime.strptime('2019-June', '%Y-%B')

    @staticmethod
    def get_full_name_from_url(url):
        return url.replace('https://api.github.com/repos/', '')

    @staticmethod
    def convert_message_to_number(message):
        arr = bytes(message, 'utf-8')
        crc = zlib.crc32(arr)
        return crc

    @staticmethod
    def get_all_commits_by_author(full_name):
        try:
            print(Repository.list_authors)
            l = []
            m = list(Repository.list_authors)
            print(m)
            print(type(m))
            print(len(m))
            if len(m) != 0:
                for j in m:
                    print(f"getting pull requests commits that author {j}")
                    for i in range(1, 1000):
                        try:
                            print("page = ", i, j)
                            url = f'https://api.github.com/repos/{full_name}/commits?per_page=100&page={i}&author={j}'
                            global username, password, g_counter
                            data = requests.get(url,
                                                auth=HTTPBasicAuth(username[g_counter % 3],
                                                                   password[g_counter % 3])).json()
                            if data is not None:
                                if len(data) == 0:
                                    break
                                for k in data:
                                    commit_date = k['commit']['author']['date']
                                    if Repository.june_2019 >= datetime.strptime(commit_date, '%Y-%m-%dT%XZ'):
                                        l.append([k['commit']['message'], commit_date])
                        except Exception as e:
                            print(Fore.RED + str(e))
                            print(Back.BLUE + 'error')
                            print(Style.RESET_ALL)
                Repository.list_authors.clear()
                return l
        except Exception as e:
            print(Fore.RED + str(e))
            print(Back.BLUE + 'error')
            print(Style.RESET_ALL)

    @staticmethod
    def get_all_pull_commits(url):
        data = requests.get(url, auth=HTTPBasicAuth(username[g_counter % 3], password[g_counter % 3])).json()
        l = []
        commits_sha = []
        if data is not None:
            if len(data) == 0:
                return l, commits_sha
            else:
                for k in data:
                    print(f'commit sha: {k["sha"]}, {url}')
                    commits_sha.append(k['sha'])
                    l.append(k['commit']['message'])
        return l, commits_sha

    @staticmethod
    def get_pull_requests_commits(full_name):
        try:
            print(f"getting pull requests commits of {full_name}")
            l = []
            for i in range(1, 1000):
                try:
                    print("page = ", i, full_name)
                    url = f'https://api.github.com/repos/{full_name}/pulls?per_page=100&page={i}&state=closed'
                    global username, password, g_counter
                    data = requests.get(url,
                                        auth=HTTPBasicAuth(username[g_counter % 3], password[g_counter % 3])).json()
                    if data is not None:
                        if len(data) == 0:
                            return l
                        for k in data:
                            _from = k['head']['label']
                            list_of_commits, list_commits_sha = Repository.get_all_pull_commits(k['commits_url'])
                            pull_date = k['created_at']
                            if Repository.june_2019 >= datetime.strptime(pull_date, '%Y-%m-%dT%XZ'):
                                l.append([k['title'], pull_date, _from, list_of_commits, list_commits_sha, k['id']])
                                Repository.list_authors.add(k['user']['login'])

                except Exception as e:
                    print(Fore.RED + str(e))
                    print(Back.BLUE + 'error')
                    print(Style.RESET_ALL)
                    return l
        except Exception as e:
            print(Fore.RED + str(e))
            print(Back.BLUE + 'error')
            print(Style.RESET_ALL)
            return l

    @staticmethod
    def get_commits_date(full_name):
        try:
            print(f"getting commits of {full_name}")
            l = []
            for i in range(1, 1000):
                try:
                    print("page = ", i, full_name)
                    url = f'https://api.github.com/repos/{full_name}/commits?per_page=100&page={i}'
                    global username, password, g_counter
                    data = requests.get(url,
                                        auth=HTTPBasicAuth(username[g_counter % 3], password[g_counter % 3])).json()
                    if data is not None:
                        if len(data) == 0:
                            return l
                        for k in data:
                            commit_date = k['commit']['author']['date']
                            if Repository.june_2019 >= datetime.strptime(commit_date, '%Y-%m-%dT%XZ'):
                                l.append([k['commit']['message'], k['commit']['author']['name'], commit_date, k['sha']])
                except Exception as e:
                    print(Fore.RED + str(e))
                    print(Back.BLUE + 'error')
                    print(Style.RESET_ALL)
                    return l
        except Exception as e:
            print(Fore.RED + str(e))
            print(Back.BLUE + 'error')
            print(Style.RESET_ALL)
            return l

    @staticmethod
    def repo_info(full_name):
        try:
            dict_total = dict()
            global g, g_counter, username, password
            repo = g.get_repo(full_name)
            g_counter += 1
            g = Github(username[g_counter % 3], password[g_counter % 3])
            parent_commit = None
            parent_pull_request = None
            parent_pull_request_author = None
            parent_full_name = None
            len_parent_commit = None
            if repo.parent:

                print("----------------------------this repository has a parent------------------------------------")
                parent_full_name = repo.parent.full_name
                if Repository.parent_commits_dict.get(parent_full_name):

                    parent_commit = Repository.parent_commits_dict.get(parent_full_name)
                    parent_pull_request = Repository.parent_pulls_requests.get(parent_full_name)
                    parent_pull_request_author = Repository.parent_pulls_requests_authors.get(parent_full_name)

                else:

                    parent_commit = Repository.get_commits_date(parent_full_name)
                    Repository.parent_commits_dict[parent_full_name] = parent_commit
                    print('getting parent_commit finished')
                    parent_pull_request = Repository.get_pull_requests_commits(parent_full_name)
                    Repository.parent_pulls_requests[parent_full_name] = parent_pull_request
                    print('getting parent_pull_request finished')
                    parent_pull_request_author = Repository.get_all_commits_by_author(parent_full_name)
                    Repository.parent_pulls_requests_authors[parent_full_name] = parent_pull_request_author
                    print('getting parent_pulls_requests_authors finished')

                len_parent_commit = len(parent_commit)
                repo_commit = Repository.get_commits_date(full_name)

            else:

                repo_commit = Repository.get_commits_date(full_name)
                Repository.parent_commits_dict[full_name] = repo_commit
                print('getting parent_commit finished')
                parent_pull_request = Repository.get_pull_requests_commits(full_name)
                Repository.parent_pulls_requests[full_name] = parent_pull_request
                print('getting parent_pull_request finished')
                parent_pull_request_author = Repository.get_all_commits_by_author(full_name)
                Repository.parent_pulls_requests_authors[full_name] = parent_pull_request_author
                print('getting parent_pulls_requests_authors finished')
            if repo.parent:
                Repository.write_to_excel_file(
                    full_name,
                    repo_commit,
                    parent_commit,
                    parent_pull_request,
                    parent_pull_request_author
                )
            print("----------------------------getting repository finished--------------------------------------")
            return dict_total
        except Exception as e:
            print(Fore.RED + str(e))
            print(Back.BLUE + 'error')
            print(Style.RESET_ALL)

    @staticmethod
    def write_to_excel_file(
            full_name,
            repo_commit,
            parent_commit,
            parent_pull_request,
            parent_pull_request_author
    ):

        file_name = full_name
        file_name = file_name.replace('/', '_') + '.xlsx'
        print(f'writing to excel file {file_name}')
        # if not os.path.isfile(file_name):
        excel_file = xlsxwriter.Workbook(file_name)

        # create sheet parent commit that contains messages and authors

        sheet_parent_commits = excel_file.add_worksheet("parent_commit")
        sheet_parent_commits.write(0, 0, 'commit_message')
        sheet_parent_commits.write(0, 1, 'author_name')
        sheet_parent_commits.write(0, 2, 'date')
        sheet_parent_commits.write(0, 3, 'id')
        row = 1
        for item in parent_commit:
            sheet_parent_commits.write(row, 0, item[0])
            sheet_parent_commits.write(row, 1, item[1])
            sheet_parent_commits.write(row, 2, item[2])
            sheet_parent_commits.write(row, 3, item[3])
            row += 1
        print(f'saving parent_commits on file {file_name}')
        # create sheet repo commit that contains messages and authors

        sheet_repo_commits = excel_file.add_worksheet("repo_commit")
        sheet_repo_commits.write(0, 0, 'commit_message')
        sheet_repo_commits.write(0, 1, 'author_name')
        sheet_repo_commits.write(0, 2, 'date')
        sheet_repo_commits.write(0, 3, 'id')
        row = 1
        for item in repo_commit:
            sheet_repo_commits.write(row, 0, item[0])
            sheet_repo_commits.write(row, 1, item[1])
            sheet_repo_commits.write(row, 2, item[2])
            sheet_repo_commits.write(row, 3, item[3])
            row += 1
        print(f'saving repo_commits on file {file_name}')
        # create sheet parent pull requests that contains messages

        sheet_parent_pull_request = excel_file.add_worksheet("parent_pull_request")
        sheet_parent_pull_request.write(0, 0, 'message')
        sheet_parent_pull_request.write(0, 1, 'date')
        sheet_parent_pull_request.write(0, 2, 'from')
        sheet_parent_pull_request.write(0, 3, 'list of commit that they contain')
        row = 1
        for item in parent_pull_request:
            sheet_parent_pull_request.write(row, 0, item[0])
            sheet_parent_pull_request.write(row, 1, item[1])
            sheet_parent_pull_request.write(row, 2, item[2])
            sheet_parent_pull_request.write(row, 3, str(item[3]))
            row += 1
        print(f'saving parent_pull_request on file {file_name}')

        # create sheet parent pull requests id with commits sha

        sheet_parent_pull_request = excel_file.add_worksheet("pull_id_and_commits_id")
        sheet_parent_pull_request.write(0, 0, 'pull_id')
        sheet_parent_pull_request.write(0, 1, 'commit_sha')
        row = 1
        for item in parent_pull_request:
            for commit in item[4]:
                sheet_parent_pull_request.write(row, 0, str(item[5]))
                sheet_parent_pull_request.write(row, 1, str(commit))
                row += 1
        print(f'saving pull_id_and_commits_id on file {file_name}')

        # create sheet parent pull requests by authors that contains messages

        sheet_parent_pull_request_author = excel_file.add_worksheet("parent_pull_request_author")
        sheet_parent_pull_request_author.write(0, 0, 'message')
        sheet_parent_pull_request_author.write(0, 1, 'date')
        row = 1
        for item in parent_pull_request_author:
            sheet_parent_pull_request_author.write(row, 0, item[0])
            sheet_parent_pull_request_author.write(row, 1, item[1])
            row += 1
        print(f'saving parent_pull_request_author on file {file_name}')

        excel_file.close()

    @staticmethod
    def has_repo(full_name):
        file_name = full_name
        file_name = file_name.replace('/', '_') + '.xlsx'
        print(os.path.isfile(file_name))
        return os.path.isfile(file_name)


if __name__ == '__main__':
    try:
        r = Repository()
        loc = ("data_1.xlsx")
        wb = xlrd.open_workbook(loc)
        sheet = wb.sheet_by_index(0)
        sheet.cell_value(0, 0)
        keys = ['full_name', 'repo_id', 'forked_from', 'parent_id', 'hf_parent_id', 'depth', 'hf_depth', 'is_hard_fork',
                'url', 'counter', 'percent', 'num_of_commits']
        reposes_inf = []
        for i in range(1, sheet.nrows):
            try:
                repo_dict = {}
                full_name = Repository.get_full_name_from_url(str(sheet.cell_value(i, 7)))
                print('checking repo: {}'.format(full_name))
                # if not r.has_repo(full_name):
                print('starting getting repo: {}'.format(full_name))
                repo_id = str(sheet.cell_value(i, 0))
                forked_from = str(sheet.cell_value(i, 1))
                parent_id = str(sheet.cell_value(i, 2))
                hf_parent_id = str(sheet.cell_value(i, 3))
                depth = str(sheet.cell_value(i, 4))
                hf_depth = str(sheet.cell_value(i, 5))
                is_hard_fork = str(sheet.cell_value(i, 6))
                url = str(sheet.cell_value(i, 7))
                print(full_name)
                repo_dict['full_name'] = full_name
                repo_dict['repo_id'] = repo_id
                repo_dict['forked_from'] = forked_from
                repo_dict['parent_id'] = parent_id
                repo_dict['hf_parent_id'] = hf_parent_id
                repo_dict['depth'] = depth
                repo_dict['hf_depth'] = hf_depth
                repo_dict['is_hard_fork'] = is_hard_fork
                repo_dict['url'] = url
                pub_info = r.repo_info(full_name)
                time.sleep(40)
                for j, key in enumerate(pub_info):
                    repo_dict[key] = str(pub_info[key])
                reposes_inf.append(repo_dict)
                with open('get_1.json', 'a') as f:
                    f.writelines(str(repo_dict) + "\n")
                print(str(repo_dict) + "\n" + str(i))
            except Exception as e:
                print(Fore.RED + str(e))
                print(Back.BLUE + 'error')
                print(Style.RESET_ALL)
            workbook = xlsxwriter.Workbook('get_1.xlsx')
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
