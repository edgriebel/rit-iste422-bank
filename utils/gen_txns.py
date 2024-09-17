import datetime
import random
from typing import Any, List, Sequence, Tuple
import os
from collections import defaultdict

PATH = "src/main/resources/production_data/"
DELIM = "!"


def get_word() -> str:
    fname = "/usr/share/dict/words"
    size = os.stat(fname).st_size
    with open("/usr/share/dict/words") as f:
        f.seek(random.randint(1, size - 1000))
        f.readline()
        word = None
        while not word:
            word = f.readline().strip()
            if word.capitalize() == word or len(word) <= 5 or len(word) > 20:
                # print(f"Skipping word {word}")
                word = None

    return word


def read_owners(filename: str) -> Sequence[Sequence[str]]:
    with open(filename) as f:
        recs = f.readlines()
    return [list(map(str.strip, r.split(DELIM))) for r in recs][1:]


def save_accts(checking, savings) -> None:
    with open(PATH + "checking.csv", "w") as cf, open(PATH + "savings.csv", "w") as sf:
        cf.write(
            DELIM.join("id,name,balance,checkNumber,ownerId,version".split(",")) + "\n"
        )
        sf.write(
            DELIM.join("id,name,balance,interestRate,ownerId,version".split(",")) + "\n"
        )
        for c in checking:
            rec = f'{c[0]:>5},{c[1]+",":30}{c[2]:-8},{c[3]:-5},{c[4]},v1'
            cf.write(DELIM.join(rec.split(",")) + "\n")
        for s in savings:
            rec = f'{s[0]:>5},{s[1]+",":30}{s[2]:-8},{s[3]:-5},{s[4]},v1'
            sf.write(DELIM.join(rec.split(",")) + "\n")


def get_accounts(owners: Sequence[Sequence[str]]) -> Tuple[List[str], List[str]]:
    ck = []  # defaultdict(list)
    sav = []  # defaultdict(list)

    for o in owners:
        print(f"===== {o[1]} =====")
        acct_type = random.randint(1, 10)
        # 20% we get just checking, 20% just savings, 60% we get both
        if acct_type in [1, 2, 5, 6, 7, 8, 9, 10]:
            # Checking
            acct_id = random.randint(100, 99999)
            name = f"{get_word()} {get_word()}"
            balance = random.randint(3300, 2500000) / 100
            ck_no = random.randint(100, 5000)
            o_id = o[0]
            rec = [acct_id, name, balance, ck_no, o_id, "v1"]
            print(f"checking: {rec}")
            ck.append(rec)

        if acct_type >= 3:
            # Savings
            acct_id = random.randint(100, 99999)
            name = f"{get_word()} {get_word()}"
            balance = random.randint(3300, 2500000) / 100
            rate = random.randint(1, 1200) / 100
            o_id = o[0]
            rec = [acct_id, name, balance, rate, o_id, "v1"]
            print(f"savings: {rec}")
            sav.append(rec)
    return (ck, sav)


OPEN = "OPEN"
CHECK = "CHECK"
DEPOSIT = "deposit"
WD = "W/D"


def save_txns(txns: Sequence[Any]) -> Sequence[str]:
    """
    Input Format: acct_id, txn_type, amount, date
    Format in file: id,accountId,entryName,amount,date,version
    """
    # Sort records by txn date
    txns = sorted(sum(txns, []), key=lambda r: str(r[3]))

    rec_id = random.randint(1, 1000)
    recs = []
    with open(PATH + "register.csv", "w") as rf:
        rf.write("id,accountId,entryName,amount,date,version\n")
        for t in txns:
            rec = f"{rec_id:-6},{t[0]:-8},{t[1]:>8},{t[2]:-9.2f},{t[3]},v1"
            recs.append(rec)
            rf.write(DELIM.join(rec.split(",")) + "\n")
            rec_id += 1
    return recs


# Return: acct_id, txn_type, amount, date
def get_txns(
    acct: Sequence, is_checking: bool
) -> Tuple[float, Sequence[Sequence[Any]]]:
    amounts = []
    total, start = 0.0, 0.0
    acct_id = acct[0]
    for _ in range(random.randint(5, 30)):
        amt = random.randint(1_00, 1000_00) / 100.0
        if random.randint(1, 100) > 50:
            amt *= -1.0
        amounts.append(amt)
        total += amt
        if total < 0:
            diff = 0 - total
            print(
                f"Adjusting starting bal: amt {amt:0.2f} tot={total:0.2f} diff={diff:0.2f} start={start:0.2f} + {diff:0.2f} = {start+diff:0.2f}"
            )
            total = 0
            start += diff

    print(f"Acct id {acct_id}: generated {len(amounts)}")
    # now generate the records
    txns = []
    txns.append([acct_id, OPEN, start])
    for val in amounts:
        txn_type = DEPOSIT if val > 0 else WD
        if is_checking and val < 0 and random.randint(1, 100) > 33:
            txn_type = CHECK
        txns.append([acct_id, txn_type, val])

    # now go back and populate dates, transactions are <= 180d apart
    dt = datetime.datetime.now()
    for t in txns[::-1]:
        dt -= datetime.timedelta(random.randint(1, 180))
        t.append(int(dt.timestamp() * 1000) + random.randint(1, 1000))

    print(f"account: {acct}\ntxns: count{len(txns)}\n{txns}\n==========")
    return total, txns


def do_it():
    print("\n=================== CREATE OWNERS ====================\n")
    owners = create_owners()
    # save_owners(owners)

    # owners = [o.split(DELIM) for o in owners]
    owners = read_owners("src/main/resources/production_data/owners.csv")
    print("\n=================== CREATE ACCOUNTS ====================\n")
    ck, sav = get_accounts(owners)

    all_txns = []
    print("\n=================== CREATE TRANSACTIONS ====================\n")
    for c in ck:
        print(f"Creating txns for {c}")
        end_bal, txns = get_txns(c, True)
        # add dates (now? later?)
        all_txns.append(txns)
        # reset ending balance to
        c[2] = end_bal

    for s in sav:
        print(f"Creating txns for {s}")
        end_bal, txns = get_txns(s, True)
        # add dates (now? later?)
        all_txns.append(txns)
        # reset ending balance to
        s[2] = end_bal

    [print("c:", r) for r in ck]
    [print("s:", r) for r in sav]
    save_accts(ck, sav)

    print("Transations ------------")
    print(all_txns)
    save_txns(all_txns)


def gen_dob():
    dob = datetime.datetime(
        random.randint(1940, 2010), random.randint(1, 12), random.randint(1, 28)
    )
    return int(dob.timestamp() * 1000) + random.randint(1, 1000)


def save_owners(owners: Sequence[str]) -> None:
    """
    Output format: id,name,dob,ssn,address,address2,city,state,zip,version
    """
    with open(PATH + "owners.csv", "w") as of:
        of.write("id,name,dob,ssn,address,address2,city,state,zip,version\n")
        for o in owners:
            of.write(o + "\n")


def create_owners() -> Sequence[str]:
    owners = [
        "12346, Hilda Schrader Whitcher, 490075200750, 078-05-1120, 33 Main St, , Lockport, NY, 14094, v1"
    ]

    # id,name,dob,ssn,address,address2,city,state,zip,version
    for i in range(10):
        owner_id = f"{random.randint(10000,99999)}"
        ssn = f"{random.randint(900, 999)}-{random.randint(0,99):02}-{random.randint(0,1000):04}"
        name = names[i]
        dob = gen_dob()
        addr = addresses[i]
        assert len(addr) == 4, f"{addr}th element too small"
        owners.append(
            f"{owner_id}, {name}, {dob}, {ssn}, {addr[0]}, , {addr[1]}, {addr[2]}, {addr[3]}, v1"
        )
        print(owners[-1])
    return owners


names = [
    "Hope Kent",
    "Monica Wilcox",
    "Delilah Barker",
    "Keyaan Carey",
    "Casey Finley",
    "Aron Stevens",
    "Jason Watson",
    "Isabelle Lowery",
    "Lillian Mccann",
    "Ameer Russo",
]

addresses = [
    ("8303 South Lake View St.", "Chicopee", "MA", "01020"),
    ("50 Prairie Street", "Oxnard", "CA", "93035"),
    ("7116 Thomas Avenue", "Altamonte Springs", "FL", "32714"),
    ("879 Westminster Street", "Riverside", "NJ", "08075"),
    ("88 Delaware St.", "Parlin", "NJ", "08859"),
    ("270 Washington Lane", "Grand Island", "NE", "68801"),
    ("40 Lake View Rd.", "Glenside", "PA", "19038"),
    ("88 Bishop Circle", "Granger", "IN", "46530"),
    ("7733 Coffee Street", "Williamstown", "NJ", "08094"),
    ("72 Tallwood Street", "Kings Mountain", "NC", "28086"),
]

if __name__ == "__main__":
    do_it()
