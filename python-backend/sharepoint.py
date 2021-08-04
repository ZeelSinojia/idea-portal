import requests


def accessToken():
    url = "https://accounts.accesscontrol.windows.net/49faa174-bfba-4cb6-b0fb-d45d65fa3c29/tokens/OAuth/2"

    payload={'grant_type': 'client_credentials',
    'client_id': '60d472f9-3ae1-40e1-aded-f603f732cf86@49faa174-bfba-4cb6-b0fb-d45d65fa3c29',
    'client_secret': 'pUK2wdxMv8j3VakDVVVS2m4+quy26cYPkgH6IB26clc=',
    'resource': '00000003-0000-0ff1-ce00-000000000000/teamcodered.sharepoint.com@49faa174-bfba-4cb6-b0fb-d45d65fa3c29'}
    files=[

    ]
    headers = {
    'Content-Type': 'application/json;odata=verbose',
    'Accept': 'application/json;odata=verbose',
    'Cookie': 'esctx=AQABAAAAAAD--DLA3VO7QrddgJg7Wevru9JbWOcXVZ0O4MZZet8qkOPuNyk8_f8ur0Ry8YeBClg_ilcKgZojAW82p016rfbI7yA32qmQBiWib10BtPfzd6Tticj4aMoe-mr1rxknrTIczPO90QtVRYLJ48vSpM-LcFkGZOeopmSigC5sPrpAFEhQcXV0pjq2HpuzSvLpX6YgAA; fpc=Alq7kCO7SFRFpnvxHpey0SwBxzKrAQAAAKuiNdgOAAAA; stsservicecookie=estsfd; x-ms-gateway-slice=estsfd'
    }

    response = requests.request("GET", url, headers=headers, data=payload, files=files)
    return(response.text)


def createFolder(folder_name, accessToken):

    url = "https://teamcodered.sharepoint.com/sites/SEProductions/_api/web/folders"

    payload = "{\r\n  \"__metadata\": {\r\n\t\"type\": \"SP.Folder\"\r\n  },\r\n  \"ServerRelativeUrl\": \"trial_1/" + folder_name + "\"\r\n}\r\n"
    headers = {
    'Authorization': 'Bearer ' + accessToken,
    'Accept': 'application/json;odata=verbose',
    'Content-Type': 'application/json;odata=verbose',
    'Content-Length': '95',
    'X-RequestDigest': '0x9E7831356DD8192D04AA937F42644C3A1D0B2AC289C8462EE1576F1AD7236D072A0B6133BD06C230F17B4B2E8B69B5107B483DDD42893ED1C62B851AD5EC451E,12 May 2021 01:49:43 -0000'
    }

    response = requests.request("POST", url, headers=headers, data=payload)

    print(response.text)

def createTheme(file):
    filename = "'" + file.filename + "'"
    url = "https://teamcodered.sharepoint.com/sites/SEProductions/_api/web/GetFolderByServerRelativeUrl('trial_1/folderB/')/Files/add(url=" + filename + ",overwrite=true)"

    payload=file
    files=[

    ]
    headers = {
    'Authorization': 'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6Im5PbzNaRHJPRFhFSzFqS1doWHNsSFJfS1hFZyIsImtpZCI6Im5PbzNaRHJPRFhFSzFqS1doWHNsSFJfS1hFZyJ9.eyJhdWQiOiIwMDAwMDAwMy0wMDAwLTBmZjEtY2UwMC0wMDAwMDAwMDAwMDAvdGVhbWNvZGVyZWQuc2hhcmVwb2ludC5jb21ANDlmYWExNzQtYmZiYS00Y2I2LWIwZmItZDQ1ZDY1ZmEzYzI5IiwiaXNzIjoiMDAwMDAwMDEtMDAwMC0wMDAwLWMwMDAtMDAwMDAwMDAwMDAwQDQ5ZmFhMTc0LWJmYmEtNGNiNi1iMGZiLWQ0NWQ2NWZhM2MyOSIsImlhdCI6MTYyMTkxMzMwMiwibmJmIjoxNjIxOTEzMzAyLCJleHAiOjE2MjIwMDAwMDIsImlkZW50aXR5cHJvdmlkZXIiOiIwMDAwMDAwMS0wMDAwLTAwMDAtYzAwMC0wMDAwMDAwMDAwMDBANDlmYWExNzQtYmZiYS00Y2I2LWIwZmItZDQ1ZDY1ZmEzYzI5IiwibmFtZWlkIjoiNjBkNDcyZjktM2FlMS00MGUxLWFkZWQtZjYwM2Y3MzJjZjg2QDQ5ZmFhMTc0LWJmYmEtNGNiNi1iMGZiLWQ0NWQ2NWZhM2MyOSIsIm9pZCI6IjE5Zjc4MjI0LWNjZjYtNGVlMC04ZTJmLWExOWM1ZGM0YTM5ZCIsInN1YiI6IjE5Zjc4MjI0LWNjZjYtNGVlMC04ZTJmLWExOWM1ZGM0YTM5ZCIsInRydXN0ZWRmb3JkZWxlZ2F0aW9uIjoiZmFsc2UifQ.lJQnwuw6a7MVrXhVFH3gpgGDnobAvlaQabBjZ6JjDCJaa4V95bjqYWGGJvpke4TDNRSezFqMOk8TVCjlqBa7rQLGffnNzl0UuoZDgutK9froseM8eA9VdYsNUh9KG4K29ddbK63enQmr990DGV1ZIWooMQCOzrFPX4ZCqJ6E_Q-0DayYzJgzBINtgAkdJp6BwtVTaJ_qE7nz-JkXY1RsTjMMIDL0OS_5Yd67B_VJ0kLAU-WTL1MASSrtkLzakwrb30Ru6df3KStBhD6k3ymeycyanYItL7NPIbFXNOCQS4aVW3DyPHeZcoqflufkPTBVFBW5iO1cJuhSWMFBQiQm5w',
    'X-RequestDigest': '0x9E7831356DD8192D04AA937F42644C3A1D0B2AC289C8462EE1576F1AD7236D072A0B6133BD06C230F17B4B2E8B69B5107B483DDD42893ED1C62B851AD5EC451E,12 May 2021 01:49:43 -0000'
    }

    response = requests.request("POST", url, headers=headers, data=payload, files=files)

    # print(response.text)


