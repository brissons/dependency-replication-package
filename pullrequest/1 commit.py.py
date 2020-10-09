import time

import requests
import xlrd
import xlsxwriter
from requests.auth import HTTPBasicAuth
from github import Github
from colorama import Fore, Back, Style
import zlib

g = Github('soghac', 'AbCd1360')


class Repository:
    parent_commits_dict = dict()
    parent_pulls_requests_msg = dict()
    parent_pulls_requests_user = dict()
    # parent_pulls_requests_authors = dict()
    list_authors = set()

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
            for j in Repository.list_authors:
                print(f"getting pull requests commits that author {j}")
                for i in range(1, 1000):
                    try:
                        print("page = ", i, j)
                        url = f'https://api.github.com/repos/{full_name}/commits?per_page=100&page={i}&author={j}'
                        data = requests.get(url,
                                            auth=HTTPBasicAuth('soghac', 'AbCd1360')).json()
                        if data is not None:
                            if len(data) == 0:
                                break
                            for k in data:
                                l.append(k['commit']['message'])
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
    def get_pull_requests_commits(full_name):
        try:
            print(f"getting pull requests commits of {full_name}")
            l = []
            for i in range(1, 1000):
                try:
                    print("page = ", i, full_name)
                    url = f'https://api.github.com/repos/{full_name}/pulls?per_page=100&page={i}&state=closed'
                    data = requests.get(url, auth=HTTPBasicAuth('soghac', 'AbCd1360')).json()
                    if data is not None:
                        if len(data) == 0:
                            return l
                        for k in data:
                            if k.get('merged_at', None):
                                l.append([k['title'], k['user']['login']])
                                Repository.list_authors.add(k['user']['login'])
                except Exception as e:
                    print(Fore.RED + str(e))
                    print(Back.BLUE + 'error')
                    print(Style.RESET_ALL)
        except Exception as e:
            print(Fore.RED + str(e))
            print(Back.BLUE + 'error')
            print(Style.RESET_ALL)

    @staticmethod
    def get_commits_date(full_name):
        try:
            print(f"getting commits of {full_name}")
            l = []
            for i in range(1, 1000):
                try:
                    print("page = ", i, full_name)
                    url = f'https://api.github.com/repos/{full_name}/commits?per_page=100&page={i}'
                    data = requests.get(url, auth=HTTPBasicAuth('soghac', 'AbCd1360')).json()
                    if data is not None:
                        if len(data) == 0:
                            return l
                        for k in data:
                            l.append([k['commit']['message'], k['commit']['author']['name']])
                except Exception as e:
                    print(Fore.RED + str(e))
                    print(Back.BLUE + 'error')
                    print(Style.RESET_ALL)
        except Exception as e:
            print(Fore.RED + str(e))
            print(Back.BLUE + 'error')
            print(Style.RESET_ALL)

    @staticmethod
    def repo_info(full_name):
        try:
            dict_total = dict()
            repo = g.get_repo(full_name)
            parent_pull_request_msg = None
            parent_pull_request_user = None
            len_parent_commit = None
            if repo.parent:

                print("----------------------------this repository has a parent------------------------------------")
                parent_full_name = repo.parent.full_name
                if Repository.parent_pulls_requests_msg.get(parent_full_name, None):
                    print(
                        '__________________________________________we had pull requests of parent__________________________________________')
                    parent_pull_request_msg = Repository.parent_pulls_requests_msg.get(parent_full_name)
                    parent_pull_request_user = Repository.parent_pulls_requests_user.get(parent_full_name)

                else:
                    print(
                        '__________________________________________we searcg pull requests of parent__________________________________________')
                    parent_pull_request_msg = Repository.get_pull_requests_commits(parent_full_name)
                    tmp1 = []
                    tmp2 = []
                    for i in range(len(parent_pull_request_msg)):
                        tmp1.append(parent_pull_request_msg[i][0])
                        tmp2.append(parent_pull_request_msg[i][1])
                    parent_pull_request_msg = tmp1
                    parent_pull_request_user = tmp2
                    Repository.parent_pulls_requests_msg[parent_full_name] = parent_pull_request_msg
                    Repository.parent_pulls_requests_user[parent_full_name] = parent_pull_request_user
                print(
                    '__________________________________________we search commit of child__________________________________________')
                repo_commit = Repository.get_commits_date(full_name)

            else:
                print(
                    '__________________________________________we search for pull request of parent and this repo is a parent__________________________________________')
                repo_commit = []
                # Repository.parent_commits_dict[full_name] = repo_commit
                parent_pull_request_msg = Repository.get_pull_requests_commits(full_name)
                tmp1 = []
                tmp2 = []
                for i in range(len(parent_pull_request_msg)):
                    tmp1.append(parent_pull_request_msg[i][0])
                    tmp2.append(parent_pull_request_msg[i][1])
                parent_pull_request_msg = tmp1
                parent_pull_request_user = tmp2
                Repository.parent_pulls_requests_msg[full_name] = parent_pull_request_msg
                Repository.parent_pulls_requests_user[full_name] = parent_pull_request_user

            len_repo_commits = len(repo_commit)
            dict_total['repo_id'] = str(repo.id)
            print(parent_pull_request_msg)
            print('+' * 100)
            print('len_repo_commits   =  ' + str(len_repo_commits))
            counter = 0
            if repo.parent:
                owner_login = repo.owner.login
                owner_name = repo.owner.name
                print(parent_pull_request_user)
                if owner_login in parent_pull_request_user:
                    for i in range(len_repo_commits):
                        print(repo_commit[len_repo_commits - 1 - i][0])
                        print(str(repo_commit[len_repo_commits - 1 - i][1]) + "====\t====" + str(owner_name))
                        if repo_commit[len_repo_commits - 1 - i][0] in parent_pull_request_msg and \
                                repo_commit[len_repo_commits - 1 - i][1] == owner_name:
                            counter += 1
                            print(repo_commit[len_repo_commits - 1 - i])
                            print('boooood', i + 1)
                            break
                if counter == 1:
                    dict_total['social'] = 'social'
                else:
                    dict_total['social'] = 'Hard'

            else:
                dict_total['social'] = "This repo is a parent"

            print("----------------------------getting repository finished--------------------------------------")
            return dict_total

        except Exception as e:
            print(Fore.RED + str(e))
            print(Back.BLUE + 'error')
            print(Style.RESET_ALL)


if __name__ == '__main__':
    try:
        r = Repository()
        loc = ("data2.xlsx")
        wb = xlrd.open_workbook(loc)
        sheet = wb.sheet_by_index(0)
        sheet.cell_value(0, 0)
        keys = ['full_name', 'repo_id', '1 commit']
        reposes_inf = []
        for i in range(1, sheet.nrows):
            try:
                repo_dict = {}
                full_name = Repository.get_full_name_from_url(str(sheet.cell_value(i, 0)))
                print(full_name)
                repo_dict['full_name'] = full_name
                pub_info = r.repo_info(full_name)
                time.sleep(40)
                for j, key in enumerate(pub_info):
                    repo_dict[keys[j + 1]] = str(pub_info[key])
                reposes_inf.append(repo_dict)
                with open('GIT1.json', 'a') as f:
                    f.writelines(str(repo_dict) + "\n")
                print(str(repo_dict) + "\n" + str(i))
            except Exception as e:
                print(Fore.RED + str(e))
                print(Back.BLUE + 'error')
                print(Style.RESET_ALL)
        workbook = xlsxwriter.Workbook('GIT2.xlsx')
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

