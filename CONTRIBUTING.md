# How to Contribute

## General workflow
0. (External contributors only) Create a fork of the repository
1. Pull any changes from `main` to make sure you're up-to-date
2. Create a branch from `main`
    * Give your branch a name that describes your change (e.g. add-scoreboard)
    * Focus on one change per branch
3. Commit your changes
    * Keep your commits small and focused
    * Write descriptive commit messages in [Conventional Commit](https://www.conventionalcommits.org/en/v1.0.0/) format
4. When you're ready, create a pull request to `main`
   * Keep your PRs small (preferably <300 LOC)
   * Format your title in [Conventional Commit](https://www.conventionalcommits.org/en/v1.0.0/) format
   * List any changes made in your description
   * Link any issues that your pull request is related to as well

### Example:
```text
Create scoreboard for total points

ADDED - Scoreboard displayed in-game at game end  
CHANGED - Updated `StorageManager` class to persist scoreboard data
```

After the pull request has been reviewed, approved, and passes all automated checks, it will be merged into main.