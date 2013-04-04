#!/bin/bash

make -C pi clean
make -C test1 clean
make -C test2 clean

make -C pi all
make -C test1 all
make -C test2 all

