package produto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.json.JSONObject;

@WebServlet("/cadProduto")
public class CadProdutoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public CadProdutoServlet() {
		super();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		try {
			// 1. Recebendo parâmetros
			String nome = request.getParameter("nome");
			String fabricante = request.getParameter("fabricante");
			String categoria = request.getParameter("categoria");
			String numSerieStr = request.getParameter("numerodeserie");
			boolean fragile = request.getParameter("fragile") != null;

			System.out.println("Recebido do formulário:");
			System.out.println("Nome: " + nome);
			System.out.println("Fabricante: " + fabricante);
			System.out.println("Categoria: " + categoria);
			System.out.println("Número de Série: " + numSerieStr);
			System.out.println("Frágil: " + fragile);

			if (nome == null || numSerieStr == null || nome.isEmpty() || numSerieStr.isEmpty()) {
				response.getWriter().write("Erro: campos obrigatórios ausentes.");
				return;
			}

			int numero_serie;
			try {
				numero_serie = Integer.parseInt(numSerieStr);
			} catch (NumberFormatException e) {
				response.getWriter().write("Erro: número de série inválido.");
				return;
			}

			// 2. Criando JSON
			JSONObject json = new JSONObject();
			json.put("nome", nome);
			json.put("fabricante", fabricante);
			json.put("categoria", categoria);
			json.put("numero_serie", numero_serie);
			json.put("fragil", fragile);

			// 3. Enviando para o webservice PHP
			URL url = new URL("http://localhost/estoquegerenciamento/cadproduto.php");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json; utf-8");
			conn.setRequestProperty("Accept", "application/json");
			conn.setDoOutput(true);

			OutputStream os = conn.getOutputStream();
			os.write(json.toString().getBytes("utf-8"));
			os.flush();
			os.close();

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
			StringBuilder responseContent = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				responseContent.append(line.trim());
			}
			br.close();

			System.out.println("Resposta do PHP: " + responseContent.toString());

			// 4. Redirecionando ou retornando sucesso
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write("<h3>Produto cadastrado com sucesso!</h3>");
			
		} catch (Exception e) {
			System.out.println("Erro no servlet: " + e.getMessage());
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write("<h3>Erro ao cadastrar produto: " + e.getMessage() + "</h3>");
		}
	}
}
