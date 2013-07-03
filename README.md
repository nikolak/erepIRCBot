
##Description

erepBot is an IRC bot that's used to parse user requested player info from [erepublik](http://www.erepublik.com) API
and return, processed/parsed, content to IRC channel.

The API requires private and public keys assigned by erepublik to function.

API documentation can be found here: http://api.erepublik.com/doc

The code is written quickly with primary foucs on "just working"; code is not optimized and/or clean.

It's published under MIT licence in hope that it will be useful to someone.

Any feedback is welcome but this isn't an active project, some major bugs will be fixed if reported/found.


##Configuration

Dependencies: pircbot, quick-json, simple-xml.

To have a functioning bot you primarily need API keys, check out api docs linked above for further info on how to obtain those.

After you get keys you need to edit src/eapi/jsonRetrieve.java file and add your public and private key.

Server/Channel, bot nick, nickname/admin password configuration is stored in bot.ini file. Edit it with any text editor.

After your bot instance has established connection you can use any command, type <bot nickname> help for a list of commands
and their descriptions.

To gain admin access to bot which enables you to make it join other channels, ban users etc type

`/msg <bot nickname> <admin password>` If you didn't define admin_password field in bot.ini file the default one is `fznxRMrW8hWK`

On first run two more files will be created for storing banned users and linked player accounts with their IRC nicknames. These are editable only from IRC.

##Licence

Copyright (c) 2013 Nikola Kovacevic <nikolak@outlook.com>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.