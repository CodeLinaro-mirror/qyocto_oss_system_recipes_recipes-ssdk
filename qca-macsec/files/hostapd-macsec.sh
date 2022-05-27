#!/bin/sh
#
# Copyright (c) 2022, Qualcomm Innovation Center, Inc. All rights reserved.
#
# Permission to use, copy, modify, and/or distribute this software for any
# purpose with or without fee is hereby granted, provided that the above
# copyright notice and this permission notice appear in all copies.
#
# THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
# WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
# MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
# ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
# WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
# ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
# OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
#

START=13

strc="eth"

start() {

	if [ $# -ne 1 ]
	then
		echo "Usage: hostapd-macsec start [ethport]"
		exit
	else
		local ethport=$1
	fi

	local result=$(echo $ethport | grep "${strc}[0-9]")

	if [ -n "$result" ]
	then
		echo "start MACsec on" $ethport
	else
		echo "please input ethernet port"
		exit
	fi

	if [ -e "/var/run/hostapd-macsec-$ethport.pid" ]
	then
		echo "There is already a MACsec PID running on this port"
		exit
	fi

	if [ -r "/etc/data/hostapd-macsec.conf" ]
	then
		echo "generate /var/run/hostapd-macsec-$ethport.conf based on /etc/data/hostapd-macsec.conf"
	else
		echo "need /etc/data/hostapd-macsec.conf"
		exit
	fi

	rm -rf /var/run/hostapd-macsec-$ethport*

	while read line
	do
		echo $line
	done < /etc/data/hostapd-macsec.conf > /var/run/hostapd-macsec-$ethport.conf

	/sbin/ip link set mtu 1536 dev $ethport

	/usr/sbin/hostapd-macsec -i $ethport -B -P /var/run/hostapd-macsec-$ethport.pid /var/run/hostapd-macsec-$ethport.conf
}

stop() {

	if [ $# -ne 1 ]
	then
		echo "Usage: hostapd-macsec stop [ethport]"
		exit
	else
		local ethport=$1
	fi

	local result=$(echo $ethport | grep "${strc}[0-9]")

	if [ -n "$result" ]
	then
		echo "stop MACsec on" $ethport
	else
		echo "please input ethernet port"
		exit
	fi

	if [ -e "/var/run/hostapd-macsec-$ethport.pid" ]
	then
		kill $(cat /var/run/hostapd-macsec-$ethport.pid) &> /dev/null
		rm -f /var/run/hostapd-macsec-$ethport.pid &> /dev/null
	else
		echo "no such PID"
	fi

	rm -rf /var/run/hostapd-macsec-$ethport*

	/sbin/ip link set mtu 1500 dev $ethport
}

status() {

	if [ $# -ne 1 ]
	then
		echo "Usage: hostapd-macsec status [ethport]"
		exit
	else
		local ethport=$1
	fi

	local result=$(echo $ethport | grep "${strc}[0-9]")

	if [ -n "$result" ]
	then
		echo "get MACsec PID status on" $ethport
	else
		echo "please input ethernet port"
		exit
	fi

	if [ -e "/var/run/hostapd-macsec-$ethport.pid" ]
	then
		local find_pid=$(cat /var/run/hostapd-macsec-$ethport.pid)
		local resa=$(pgrep /usr/sbin/hostapd-macsec | grep -wq "${find_pid}" && echo "status: Active" || echo "status: Inactive")
		echo $resa
	else
		echo "status: Inactive"
	fi
}

case "$1" in
	start)
	    start $2
	    ;;
	stop)
	    stop $2
	    ;;
	status)
	    status $2
	    ;;
	restart)
	    stop $2
	    start $2
	    ;;
	*)
	    echo "Unsupported operation: available operations (start stop restart status)"
esac
