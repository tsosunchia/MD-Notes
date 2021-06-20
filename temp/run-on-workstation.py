#!/usr/bin/python



import re
import smtplib
import urllib.parse
import urllib.request
import subprocess
from email.mime.text import MIMEText
from email.utils import formataddr

import requests
import psutil

my_sender = '844392569@qq.com'  # 发件人邮箱账号
my_pass = 'mivfmeroxsxmbeef'  # 发件人邮箱SMTP授权码
my_user = '844392569@qq.com'  # 收件人邮箱账号

import socket

def mail():
    ret = True
    #output = str(subprocess.Popen(['sudo ssh -fCNR 4513:localhost:22 -o ServerAliveInterval=60 root@123.57.183.166'],stdout=subprocess.PIPE,shell=True).communicate())
    output = str(subprocess.Popen(['ls'],stdout=subprocess.PIPE,shell=True).communicate())

    try:
        msg = MIMEText(output, 'plain', 'utf-8')
        msg['From'] = formataddr(["workstation", my_sender])  # 括号里的对应发件人邮箱昵称、发件人邮箱账号
        msg['To'] = formataddr(["gly", my_user])  # 括号里的对应收件人邮箱昵称、收件人邮箱账号
        msg['Subject'] = "run-on-workstation"

        server = smtplib.SMTP_SSL("smtp.qq.com", 465)
        server.login(my_sender, my_pass)
        server.sendmail(my_sender, [my_user, ], msg.as_string())
        server.quit()
    except Exception as e:
        ret = False
        print(e)
    return ret


if __name__ == '__main__':
    ret = mail()
    if ret:
        print("email end success")
    else:
        print("email send fail")