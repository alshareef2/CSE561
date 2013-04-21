import sys, codecs, re, datetime
from math import log

def calcEnt(tups):
  ct = float(sum([y for x,y in tups]))
  return -sum([y / ct * log(y / ct) for x, y in tups])

htRegex = re.compile("#\w+")
tmdiff = datetime.timedelta(hours=7)

dthtct = {}

ct = 0

for line in open(sys.argv[1]):
  try:
    parts = line.strip().split("\t")
    text = parts[1]
    dt = datetime.datetime.strptime(parts[3], "%Y-%m-%d %H:%M:%S.0")
    dt += tmdiff
    dtstr = dt.strftime("%Y-%m-%d %H:00")

    dthtct.setdefault(dtstr, {})

    for tag in re.findall(htRegex, text):
      dthtct[dtstr][tag] = dthtct[dtstr].get(tag, 0) + 1

    ct += 1
    if ct % 10000 == 0:
      sys.stderr.write("%d\n" % (ct,))
    if ct >= 10000000: break

  except Exception as e:
    print e
    continue

print "Date,Num. HTs,Entropy"
for dt, htct in sorted(dthtct.items(), key=lambda x:x[0]):
  print "%s,%d,%f" % (dt, len(htct), calcEnt(htct.items()))