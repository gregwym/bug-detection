#
# define ant build command
#

ANT = ant
export JAVA_TOOL_OPTIONS := -Xmx64m

.PHONY: Main

all: build

build:
	$(ANT) build

Main:
	$(ANT) Main

clean:
	$(ANT) clean

