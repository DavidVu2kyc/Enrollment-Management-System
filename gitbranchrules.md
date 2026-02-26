Recommended Git Branch Rules
Branch Structure
main
└── develop
    ├── develop-be        ← Spring Boot backend changes
    └── develop-fe        ← Frontend changes
Branch Naming Convention
Branch	Purpose
main	Stable, production-ready code
develop	Integration branch — merge BE + FE here
develop-be	Backend-only development (your Spring Boot)
develop-fe	Frontend-only development
feature/be-<name>	Specific backend features
feature/fe-<name>	Specific frontend features
fix/be-<name>	Backend bug fixes
fix/fe-<name>	Frontend bug fixes
Workflow Rules
Never commit directly to main — only merge from develop
develop-be and develop-fe branch off from develop, not main
Merge flow:
develop-be  ──►  develop  ──►  main
develop-fe  ──►  develop  ──►  main
Always pull develop before starting new work on develop-be or develop-fe to stay in sync
Quick Setup Commands
bash
# One-time setup (run from your project root)
git checkout -b develop
git checkout -b develop-be develop
git checkout -b develop-fe develop
# Daily workflow for backend
git checkout develop-be
# ... make changes ...
git add . && git commit -m "feat(be): implement AuthService login"
# Merge BE into develop when a feature is done
git checkout develop
git merge develop-be
Commit Message Convention (Recommended)
feat(be): add JWT token generation
fix(fe): correct login form validation
chore(be): update application.properties
refactor(be): extract enrollment logic to service layer