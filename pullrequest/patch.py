import requests
import xlrd
import xlsxwriter
from requests.auth import HTTPBasicAuth
from github import Github
from colorama import Fore, Back, Style
import zlib

g = Github('soghac', 'sg198202')


class Repository:
    parent_commits_dict = dict()
    parent_pulls_requests = dict()
    parent_pulls_requests_authors = dict()
    list_authors = set()

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
                for i in range(1, 500):
                    try:
                        print("page = ", i, j)
                        url = f'https://api.github.com/repos/{full_name}/commits?per_page=100&page={i}&author={j}'
                        data = requests.get(url,
                                            auth=HTTPBasicAuth('soghac', 'sg198202')).json()
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
            for i in range(1, 500):
                try:
                    print("page = ", i, full_name)
                    url = f'https://api.github.com/repos/{full_name}/pulls?per_page=100&page={i}&state=closed'
                    data = requests.get(url, auth=HTTPBasicAuth('soghac', 'sg198202')).json()
                    if data is not None:
                        if len(data) == 0:
                            return l
                        for k in data:
                            l.append([k['title']])
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
            for i in range(1, 500):
                try:
                    print("page = ", i, full_name)
                    url = f'https://api.github.com/repos/{full_name}/commits?per_page=100&page={i}'
                    data = requests.get(url, auth=HTTPBasicAuth('soghac', 'sg198202')).json()
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

                    parent_pull_request = Repository.get_pull_requests_commits(parent_full_name)
                    Repository.parent_pulls_requests[parent_full_name] = parent_pull_request

                    parent_pull_request_author = Repository.get_all_commits_by_author(parent_full_name)
                    Repository.parent_pulls_requests_authors[parent_full_name] = parent_pull_request_author

                len_parent_commit = len(parent_commit)
                repo_commit = Repository.get_commits_date(full_name)

            else:

                repo_commit = Repository.get_commits_date(full_name)
                Repository.parent_commits_dict[full_name] = repo_commit

                parent_pull_request = Repository.get_pull_requests_commits(full_name)
                Repository.parent_pulls_requests[full_name] = parent_pull_request

                parent_pull_request_author = Repository.get_all_commits_by_author(full_name)
                Repository.parent_pulls_requests_authors[full_name] = parent_pull_request_author

            len_repo_commits = len(repo_commit)
            dict_total['num_commits'] = len_repo_commits
            print(parent_pull_request_author)
            print(parent_pull_request)
            counter = 0
            if repo.parent:
                if len_repo_commits > len_parent_commit:
                    dict_total['patch'] = "Independent"
                else:
                    for i in range(len_repo_commits):
                        if not repo_commit[len_repo_commits - 1 - i][0] == parent_commit[len_parent_commit - 1 - i][0]:
                            if not repo_commit[len_repo_commits - 1 - i][1] == parent_commit[len_parent_commit - 1 - i][
                                1]:
                                if not repo_commit[len_repo_commits - 1 - i][0] in parent_pull_request:
                                    if not repo_commit[len_repo_commits - 1 - i][0] in parent_pull_request_author:
                                        print('commit number : ', i, repo_commit[len_repo_commits - 1 - i][0],
                                              "+++++++", parent_commit[len_parent_commit - 1 - i][0])
                                        counter += 1
                                        print('naboooooooood', i + 1)
                                        break
                    if counter > 0:
                        dict_total['patch'] = "Independent"
                    else:
                        dict_total['patch'] = "patch"
            else:
                dict_total['patch'] = "Independent itself"
            print("----------------------------getting repository finished--------------------------------------")
            return dict_total

        except Exception as e:
            print(Fore.RED + str(e))
            print(Back.BLUE + 'error')
            print(Style.RESET_ALL)


if __name__ == '__main__':
    try:
        r = Repository()
        loc = ("data10.xlsx")
        wb = xlrd.open_workbook(loc)
        sheet = wb.sheet_by_index(0)
        sheet.cell_value(0, 0)
        keys = ['repo_id', 'full_name', 'num_commits', 'patch']
        reposes_inf = []
        for i in range(1, sheet.nrows):
            try:
                repo_dict = {}
                full_name = str(sheet.cell_value(i, 3))
                print(full_name)
                repo_dict['repo_id'] = str(sheet.cell_value(i, 0))
                repo_dict['full_name'] = str(sheet.cell_value(i, 3))
                pub_info = r.repo_info(full_name)
                for j, key in enumerate(pub_info):
                    repo_dict[keys[j + 2]] = str(pub_info[key])
                reposes_inf.append(repo_dict)
                with open('c10.json', 'a') as f:
                    f.writelines(str(repo_dict) + "\n")
                print(str(repo_dict) + "\n" + str(i))
            except Exception as e:
                print(Fore.RED + str(e))
                print(Back.BLUE + 'error')
                print(Style.RESET_ALL)
        workbook = xlsxwriter.Workbook('GIT10.xlsx')
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
