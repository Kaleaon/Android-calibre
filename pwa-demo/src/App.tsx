// Simple test component to check if React is working
import React from 'react';

const App: React.FC = () => {
  return (
    <div style={{ padding: '20px', fontFamily: 'Arial, sans-serif' }}>
      <h1>CleverFerret Test</h1>
      <p>If you can see this, React is working!</p>
    </div>
  );
};

export default App;