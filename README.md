bcfusefs
========

Bitcasa Fuse Filesystem (based on fuse-jna)

bcfusefs is a minimalistic REST-API based Fuse Filesystem Driver for Bitcasa.

In the current revision it only supports listing and reading files, however it will be a full fledged Filesystem Driver eventually, working as a Opensource-Replacement for the official Linux Client.

Folder Structure gets cached after the initial listing calls, the timeout for the cache, along with all the other settings are configurable.

Files on the other hand are, at this moment, uncached and thus horribly slow to access. This, along with other things will get fixed in a future revision once I've figured out the best way to reliably speed up Bitcasa access without too much bloat going on in HDD / RAM.

This software is provided as is and serves as a proof of concept code.
This software is licensed under the BSD 2-Clause License and can be used freely according to it.
