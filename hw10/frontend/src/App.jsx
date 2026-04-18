import { useEffect, useState } from 'react'

function App() {
  const [message, setMessage] = useState('Loading...')

  useEffect(() => {
    fetch('/api/v1/book')
      .then(res => res.json())
      .then(data => {
        setMessage(`Hello from React! Found ${data.length} books in database`)
      })
      .catch(err => {
        setMessage('Hello from React! (Backend not connected)')
      })
  }, [])

  return (
    <div style={{ padding: '20px', fontFamily: 'Arial' }}>
      <h1>📚 Book Library</h1>
      <p>{message}</p>
      <hr />
      <h3>API endpoints ready:</h3>
      <ul>
        <li>GET /api/v1/book</li>
        <li>GET /api/v1/author</li>
        <li>GET /api/v1/genre</li>
        <li>GET /api/v1/comment?bookId=1</li>
      </ul>
    </div>
  )
}

export default App