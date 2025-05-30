# Firebase Functions for CookSmart

This directory contains the Cloud Functions for the CookSmart application.

## Setup

1. Install dependencies:
```bash
npm install
```

2. Build the functions:
```bash
npm run build
```

## Local Development

1. Start the emulator:
```bash
firebase emulators:start
```

This will start:
- Functions emulator on port 5001
- Firebase UI on port 4000

2. Using the emulator in the app:
- The app automatically detects if it's running in debug mode
- In debug mode, it will use the emulator at 10.0.2.2:5001
- In release mode, it will use production Firebase

## Deployment

1. Deploy all functions:
```bash
firebase deploy --only functions
```

2. Deploy specific function:
```bash
firebase deploy --only functions:searchMarmitonRecipes
```

## Available Functions

### searchMarmitonRecipes
- Endpoint: `/searchMarmitonRecipes`
- Method: POST
- Parameters:
  ```typescript
  {
    title?: string;
    maxTime?: number;
    difficulty?: "VERY_EASY" | "EASY" | "MEDIUM" | "HARD";
    price?: "CHEAP" | "MEDIUM" | "EXPENSIVE";
    withoutOven?: boolean;
    limit?: number;
  }
  ```
- Response:
  ```typescript
  {
    success: boolean;
    data: Array<{
      id: string;
      name: string;
      description: string;
      url: string;
      imageUrl?: string;
      duration: number;
      difficulty: string;
      price: string;
      ingredients: string[];
      steps: string[];
      rating: number;
      reviews: number;
    }>;
  }
  ```

## Error Handling

The functions return appropriate HTTP status codes:
- 200: Success
- 404: Endpoint not found
- 408: Request timeout
- 500: Server error

All errors include detailed messages in the response body.

## Debugging

To view function logs:
```bash
firebase functions:log
```

To view emulator logs:
```bash
firebase emulators:start --inspect-functions
