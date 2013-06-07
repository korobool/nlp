# This Python file uses the following encoding: utf-8
#
import os,re,sys,time

from collections import defaultdict

def individualSentences(args):
	if 'docs' in args:
		return False
	else:
		return True


class Clock:
	def __init__(self,days,hours,minutes,seconds):
		self.days = days
		self.hours = hours
		self.minutes = minutes
		self.seconds = seconds


def when():
	now = time.localtime()[2:6]
	clock = Clock(\
		now[0],\
		now[1],\
		now[2],\
		now[3]\
		)
	return clock


def runtime(start,end):
	minutes = 0
	seconds = 0
	hours = 0
	days = 0
	#
	seconds = end.seconds-start.seconds
	minutes = end.minutes - start.minutes
	hours = end.hours - start.hours
	days = end.days - start.days
	#
	if seconds < 0:
		minutes -= 1
		seconds = 60+seconds
	#
	if minutes < 0:
		hours -= 1
		minutes = 60+minutes
	#
	if hours < 0:
		days -= 1
		hours = 23+hours
	return Clock(days,hours,minutes,seconds)


def chrono(start):
	end = when()
	scriptClock = runtime(start,end)
	startTime = ':'.join(\
		[str(start.days),str(start.hours),str(start.minutes),str(start.seconds)])
	endTime = ':'.join(\
		[str(end.days),str(end.hours),str(end.minutes),str(end.seconds)])
	print 'from',startTime,'to',endTime+',',\
		'runtime was %d days, %d hours, %d minutes and %d seconds' %(\
		scriptClock.days,\
		scriptClock.hours,\
		scriptClock.minutes,\
		scriptClock.seconds\
		)


def feedbackMessage(c, i, u, l, message):
	q = ((c+1)*100)/l
	if q >= u:
		u += i
		print message,str(q)+'%'
	return u


def counter(aList):
	array = defaultdict(int)
	for item in aList:
		array[item] += 1
	counts = sorted([ (array[key], key) for key in array.keys() ], reverse=True)
	return counts