# Tempo
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)
![IRC: #shard @ esper](https://img.shields.io/badge/irc-%23shard%20%40%20irc.esper.net-ff69b4.svg)

This repository contains the code of Tempo, the core [Velocity](https://velocitypowered.com) plugin of the [Shard](https://github.com/ShardPowered) ecosystem.

## Status

In the current state, you will most likely benefit from Tempo due to its lack of configurability. I'm working to fix that however the priority for that is not that high.

## Dependencies

Tempo runs on a [Velocity](https://velocitypowered.com) server. It does heavily depend on [Snake](https://github.com/ShardPowered/snake) running on the Paper servers.

As such, all of the requirements of Snake (MongoDB, etc) are still present.

## Configuration

Snake generates its config files to `plugins/tempo/`. It uses [HOCON](https://github.com/lightbend/config/blob/master/HOCON.md) as a configuration format.

## License

- [MIT](LICENSE). 