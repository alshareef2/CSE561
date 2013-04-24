import sys, codecs, re, datetime
from math import log

tmdiff = datetime.timedelta(hours=7)

dtuser = {}
dttweet = {}

ct = 0

for line in open(sys.argv[1]):
  # try:
  parts = line.strip().split("\t")
  dt = datetime.datetime.strptime(parts[3], "%Y-%m-%d %H:%M:%S.0")
  dt += tmdiff
  dtstr = dt.strftime("%Y-%m-%d %H:00")

  dtuser.setdefault(dtstr, set([]))
  dtuser[dtstr].add(parts[2])

  dttweet.setdefault(dtstr, 0)
  dttweet[dtstr] += 1

  ct += 1
  if ct % 10000 == 0:
    sys.stderr.write("%s\n" % (ct,))
  if ct >= 10000000: break

  # except Exception as e:
    # sys.stderr.write("%s\n" % (e,))

print "Date,Users,Tweets"
for dt, users in sorted(dtuser.items(), key=lambda x:x[0]):
  print "%s,%d,%d" % (dt, len(users), dttweet[dt])