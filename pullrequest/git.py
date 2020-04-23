import requests
import xlsxwriter
import xlrd
from bs4 import BeautifulSoup
from colorama import Fore, Back, Style
from requests.auth import HTTPBasicAuth


def get_info(full_name):
    try:
        res = dict()
        data = [
            'merged',
            'closed',
            'open'
        ]
        for i in data:
            response = requests.get(f'https://github.com/{full_name}/pulls?q=is%3Apr+is%3A{i}',
                                    auth=HTTPBasicAuth('Soghac', 'sg198202'))
            print('get ' + i)
            soup = BeautifulSoup(response.content, 'html.parser')
            tag_span = soup.find_all('a', 'btn-link selected')
            s = str(tag_span[0].contents[-1]) \
                .replace('\n', '') \
                .replace('Total', '') \
                .replace('Open', '') \
                .replace('Closed', '') \
                .replace(',', '') \
                .strip()
            res[i] = s
        res['total_pr'] = int(res['closed']) + int(res['open'])
        return res
    except Exception as e:
        print(Fore.RED + str(e))
        print(Back.BLUE + 'error')
        print(Style.RESET_ALL)


if __name__ == '__main__':
    try:
        loc = ("data.xlsx")
        wb = xlrd.open_workbook(loc)
        sheet = wb.sheet_by_index(0)
        sheet.cell_value(0, 0)
        keys = ['repo_id', 'full_name', 'merged', 'closed', 'open', 'total']
        reposes_inf = []
        for i in range(1, sheet.nrows):
            try:
                repo_dict = {}
                full_name = str(sheet.cell_value(i, 3))
                print(full_name)
                repo_dict['repo_id'] = str(sheet.cell_value(i, 0))
                repo_dict['full_name'] = str(sheet.cell_value(i, 3))
                pub_info = get_info(full_name)
                for j, key in enumerate(pub_info):
                    repo_dict[keys[j + 2]] = str(pub_info[key])
                reposes_inf.append(repo_dict)
                with open('f.json', 'a') as f:
                    f.writelines(str(repo_dict) + "\n")
                print(str(repo_dict) + "\n" + str(i))
            except Exception as e:
                print(Fore.RED + str(e))
                print(Back.BLUE + 'error')
                print(Style.RESET_ALL)
        workbook = xlsxwriter.Workbook('GIT.xlsx')
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
