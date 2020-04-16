

with open("C:/Users/mahsa/Desktop/gradle.txt",mode='r') as file:
    start = False
    loop = 0
    list_dependencies = set()
    for cn, line in enumerate(file):
        if "dependencies" in line and "{" in line:
            start = True
        if "plugins" in line and "{" in line:
            start = True
        if start == True and "{" in line and "\$" not in line:
            loop += 1
        if start == True and "}" in line and "\$" not in line:
            loop -= 1
        if "}" in line and loop == 0 and start == True:
            start = False
        if start == True:
            if "//" not in line and "exclude" not in line:
                if "'" in line:
                    first = line.index("'")
                    last_index = line.rindex("'")
                    #print("{}".format(line[first:last_index].replace("'","")))
                    list_dependencies.add(line[first:last_index].replace("'",""))
                if '""' in line:
                    first = line.index('""')
                    last_index = line.rindex('""')
                    #print("{}".format(line[first:last_index].replace('""','')))
                    list_dependencies.add(line[first:last_index].replace('""',''))
    dep_string = ''
    for dep in list_dependencies:
        dep_string = dep_string+str(dep)+", "
        #print(dep)
    print(len(list_dependencies))
    print(dep_string)
