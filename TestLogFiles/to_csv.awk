#!/usr/bin/env awk -F ; -f
#
# Convert a sensor log into a CSV file suitable for plotting.
#
# The script iterates through the input file, finds records with the
# matching timestamp, and prints them as CSV lines. The input file is
# expected to be sorted by time.

BEGIN {
    if (ARGC != 2) {
        print "USAGE: ./to_csv.awk sensor.log > out.csv"
        exit 1
    }

    file = ARGV[ARGC-1]
    head["time"] = 0
    head_inv[0] = "time"
    head_len = 1
    prev_time = 0

    for (;;) {
        if ((getline < file) != 1) {
            break
        } else {
            head_len = update_head(head, head_inv, head_len)
        }
    }
    close(file)
    print to_string(head_inv)

    for (;;) {
        if ((getline < file) != 1) {
            break
        } else {
            prev_time = update_record(head, prev_time)
        }
    }

    if (frame[0] != "")
        print to_string(frame)
}

function update_head(head, head_inv, head_len) {
    type = $2

    idx = head[type]
    if (idx == "") {
        idx = head_len
        head_len++

        head[$2] = idx
        head_inv[idx] = $2
    }

    return head_len
}

function update_record(head, prev_time) {
    time = $1
    type = $2
    val = $3

    # print the current frame when timestamps don't match
    if (prev_time != 0 && prev_time != time) {
        print to_string(frame)
        delete frame
    }

    # update the current frame
    frame[0] = time
    frame[head[type]] = val

    return time
}

function to_string(frame) {
    res = sprintf("%s", frame[0])
    for (i = 1; i < head_len; i++)
            res = sprintf("%s,%s", res, frame[i])

    return res
}
