import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

function AuthorList() {
  const [authors, setAuthors] = useState([]);
  const [loading, setLoading] = useState(true);

  const loadAuthors = () => {
    fetch('/api/v1/author')
      .then(response => response.json())
      .then(data => {
        setAuthors(data);
        setLoading(false);
      });
  };

  useEffect(() => {
    loadAuthors();
  }, []);

  const handleDelete = (id, name) => {
    if (confirm(`Удалить автора "${name}"?`)) {
      fetch(`/api/v1/author/${id}`, { method: 'DELETE' })
        .then(() => loadAuthors());
    }
  };

  if (loading) return <div>Загрузка...</div>;

  return (
    <div>
      <h2>Список авторов</h2>
      <Link to="/authors/create"><button>Добавить автора</button></Link>
      
      <table border="1" cellPadding="8">
        <thead>
          <tr><th>ID</th><th>Имя</th><th>Действия</th></tr>
        </thead>
        <tbody>
          {authors.map(author => (
            <tr key={author.id}>
              <td>{author.id}</td>
              <td>{author.fullName}</td>
              <td>
                <Link to={`/authors/${author.id}`}>Просмотр</Link> |{' '}
                <Link to={`/authors/${author.id}/edit`}>Редактировать</Link> |{' '}
                <button onClick={() => handleDelete(author.id, author.fullName)}>Удалить</button>
               </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default AuthorList;