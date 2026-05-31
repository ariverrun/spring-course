import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

function BookList() {
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);

  const loadBooks = () => {
    fetch('/api/v1/book')
      .then(response => response.json())
      .then(data => {
        setBooks(data);
        setLoading(false);
      })
      .catch(error => {
        console.error('Error:', error);
        setLoading(false);
      });
  };

  useEffect(() => {
    loadBooks();
  }, []);

  const handleDelete = (id, title) => {
    if (confirm(`Удалить книгу "${title}"?`)) {
      fetch(`/api/v1/book/${id}`, { method: 'DELETE' })
        .then(() => loadBooks())
        .catch(error => console.error('Error:', error));
    }
  };

  if (loading) return <div>Загрузка...</div>;

  return (
    <div>
      <h2>Список книг</h2>
      <Link to="/books/create">
        <button>Добавить книгу</button>
      </Link>
      
      <table border="1" cellPadding="8">
        <thead>
          <tr>
            <th>ID</th><th>Название</th><th>Автор</th><th>Жанры</th>
            <th>Действия</th>
          </tr>
        </thead>
        <tbody>
          {books.map(book => (
            <tr key={book.id}>
              <td>{book.id}</td>
              <td>{book.title}</td>
              <td>{book.author.fullName}</td>
              <td>{book.genres.map(g => g.name).join(', ')}</td>
              <td>
                <Link to={`/books/${book.id}`}>Просмотр</Link> |{' '}
                <Link to={`/books/${book.id}/edit`}>Редактировать</Link> |{' '}
                <button onClick={() => handleDelete(book.id, book.title)}>Удалить</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default BookList;