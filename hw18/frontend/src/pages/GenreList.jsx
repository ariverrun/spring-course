import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

function GenreList() {
  const [genres, setGenres] = useState([]);
  const [loading, setLoading] = useState(true);

  const loadGenres = () => {
    fetch('/api/v1/genre')
      .then(response => response.json())
      .then(data => {
        setGenres(data);
        setLoading(false);
      });
  };

  useEffect(() => {
    loadGenres();
  }, []);

  const handleDelete = (id, name) => {
    if (confirm(`Удалить жанр "${name}"?`)) {
      fetch(`/api/v1/genre/${id}`, { method: 'DELETE' })
        .then(() => loadGenres());
    }
  };

  if (loading) return <div>Загрузка...</div>;

  return (
    <div>
      <h2>Список жанров</h2>
      <Link to="/genres/create"><button>Добавить жанр</button></Link>
      
      <table border="1" cellPadding="8">
        <thead><tr><th>ID</th><th>Название</th><th>Действия</th></tr></thead>
        <tbody>
          {genres.map(genre => (
            <tr key={genre.id}>
              <td>{genre.id}</td>
              <td>{genre.name}</td>
              <td>
                <Link to={`/genres/${genre.id}`}>Просмотр</Link> |{' '}
                <Link to={`/genres/${genre.id}/edit`}>Редактировать</Link> |{' '}
                <button onClick={() => handleDelete(genre.id, genre.name)}>Удалить</button>
               </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default GenreList;